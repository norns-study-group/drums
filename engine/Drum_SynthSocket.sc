/* TODO:
- FIX THE BROKEN TRIGGER. I completely don't get it.
- might need to ditch fade. idk if it's messing things up.

- Make a mixer - this thing's gonna clip
- Add another out for the sidechain
- Make a mix bus for sidechaining
- Randomize a param
- Randomize all params
- Set a param to default 
- Set all params to default 
- Pan controls?
- Effects?
- LFOs?
*/


// a primitive and limited analog to `NodeProxy`,
// which just crossfades between two stereo synth functions.
// motivation is to limit the number of active synths during crossfades,
// by specifying a shallow queuing behavior.
// @zebra

// (copied from Dronecaster)

Drum_SynthSocket {
	var server;          // a Server!
	var controls;        // Set of control names
	var <inBus;          // Array of 2 stereo Busses

	var group;           // a Group
	var <fadeSynth;      // synth to perform the xfade

	var <controlBus;     // Dictionary of control busses
	var <controlLag;     // Dictionary of control-rate lag synths

	var <source;         // synth-defining Function which is currently fading in or active
	var <sourceLast;     // the previously active source synth Function
	var <sourceQ;        // queued synth Function

	var <sourceIndex;    // current index of active source bus
	var <isFading;       // flag indicating xfade in progress

	var <env; // ext env mapped to synth param
	var <envParams;

	// do these need the <> ? idfk
	var testSynth;
	var testEnv;
	var envTrigBus;
	var envBus;
	var baseControls;

	var controlLagTime = 0.2;
	var fadeTime = 4.0;

	*new {
		arg server,      // instance of Server
		// out,             // output bus index
		out;             // output bus index
		// controls;        // array of control names; source synth functions should accept these args
		// ^super.new.init(server, out, controls);
		^super.new.init(server, out);
	}

	init {
		// arg s, out, ctl;
		// The out param seems to be the "mixer"
		// If stuff only plays thru left channel, probably need e.g. pan or !2
		arg s, out;

		"initializing. if you see a bunch of 'NODE NOT FOUND' something is probably wrong".postln;

		server = s;
		// controls = ctl.asSet;

		source = nil;
		sourceLast = nil;
		sourceQ = nil;

		sourceIndex = 0;
		isFading = false;

		"setting up group".postln;
		group = Group.new(server);
		inBus = Array.fill(2, { Bus.audio(s, 2) });

		"setting up bus".postln;
		envTrigBus = Bus.control(server, 1);
		envBus = Bus.control(server, 1);

		envParams = [\attack, \decay, \curve];

		baseControls = (
			decay: ControlSpec(0.05, 5, \exp, 0, 2, "seconds"),
			curve: ControlSpec(-10, 10, \lin, 0, -4, "exponent"),
			hz: ControlSpec(8.18, 13289.75, \exp, 0, 220, "hz"),
			attack: ControlSpec(0.001, 1, \exp, 0, 0.01, "seconds"),
		);

		"setting up test synth".postln;
		testSynth = { arg out, env=0;
			Out.ar(out, SinOsc.ar(env * 200 + 110 + 20.0.rand) / 6) 
		}.play(target:group, args: [\out, out]);

		"setting up fade synth".postln;
		fadeSynth = Array.fill(2, { arg i;
			var busIndex = inBus[i].index;
			("bus index: " ++ busIndex).postln;
			{
				arg out=0, in, gate, time=4;
				var fade, snd;
				fade = EnvGen.ar(Env.asr(time, 1, time), gate);
				snd = fade * In.ar(in, 2);
				Out.ar(out, snd)
			}.play(target:group, args: [
				\out, out,
				\in, busIndex,
				\time, fadeTime
			]);
		});

		"setting up env synth".postln;
		// Don't need a new one for each drum. Just reuse forever
		// This trigBus thing is really dumb. Maybe there's a less crappy way to do this.
		// Worst-case, could utilize the velocity bus. I forgot to add that anyway :)
		env = { arg bus, attack=0, decay=0.5, curve=(-4), hz=0.3, velocity, t_trig=1;
			var envSpec, e, trig;
			// trig = InTrig.kr(velocity);
			envSpec = Env.perc(attack, decay, curve);
			// e = EnvGen.kr(envSpec, Impulse.kr(2))
			e = EnvGen.kr(envSpec, t_trig)
			// .poll(10)
			;
			// this is just a dumb thing for testing
			// e = e + SinOsc.kr(hz, 0, 0.1).abs
			// .poll(10)
			// ;
			Out.kr(bus, e);
		}.play(target:group, args: [\bus, envBus, \velocity, envTrigBus]); // velocity should be mapped elsewhere!

		"setting up controls ".postln;
		controls = baseControls;
		this.setupControls;
		"mapping stuff.".postln;
		this.mapStuff;
	}

	mapStuff {
		Routine {
			server.sync;
			"in mapStuff. going to sync with server.".postln;
			"in mapStuff. synced with server. now to map.".postln;
			testSynth.map(\env, envBus.index);
			"mapped env to test sytnh. now mapping env controls".postln;
			envParams.do({ arg key;
				env.map(key, controlBus[key]);
			});
			"mapped env controls".postln;
			"in mapStuff. mapped stuff.".postln;
		}.play;
	}

	setupControls {
		// Routine {
			"in the setupControls routine".postln;
			"setting up controls. freeing stuff".postln;
			if (controlBus.isNil.not, {
				"control buses not nil. freeing em".postln;
				controlBus.do({ arg bus; bus.free; });
			});
			if (controlLag.isNil.not, {
				"control lags not nil. freeing em".postln;
				controlLag.do({ arg lag; lag.free; });
			});

			"maybe freed stuff".postln;

			controlBus = Dictionary.new;
			controlLag = Dictionary.new;

			"made control dictionaries; setting controls values".postln;

			controls.keys.do({ arg name;
				controlBus[name] = Bus.control(server);
				controlLag[name] = {
					arg bus, value, time = 0.2;
					ReplaceOut.kr(bus, Lag.kr(value, time));
				}.play(target:group, args:[\bus, controlBus[name].index]);
			});

			// server.sync;

			"set controls values".postln; 

		// }.play;
	}

	setSource { arg newDrum;
		if (isFading, {
			sourceQ = newDrum;
		}, {
			this.performFade(newDrum);
		});
	}

	setFadeTime { arg time;
		fadeTime = time;
		fadeSynth.do({ arg synth; synth.set(\time, fadeTime); });
	}

	trig {
		// "triggering in socket".postln;
		env.set(\t_trig, 1);
		env.set(\t_trig, 0);
		// env.set(\trig, 1);
		// env.set(\hz, 5.0.rand);
		// envTrigBus.set(1);
	}

	setControl { arg key, value;
		controlLag[key].set(\value, value);
	}

	setControlLag { arg key, time;
		// controlLag.do({ arg synth; synth.set(\time, time); });
		// It might get really tedious setting them all one at a time.
		controlLag[key].set(\time, time);
	}

	// stop {
	// 	fadeSynth.do({ arg synth; synth.set(\gate, 0); });
	// }

	free {
		group.free;
		inBus.do({ arg bus; bus.free; });
		controlBus.do({ arg bus; bus.free; });
	}

	//////////////////////////////////////////
	/// private

	// performFade { arg newFunction, args;
	performFade { arg newDrum, args;
		"performing fade".postln;
		newDrum.postln;
		sourceIndex = if (sourceIndex > 0, {0}, {1});
		// postln("performing fade; new source index = " ++ sourceIndex);

		isFading = true;
		"now we're fading".postln;

		"performFade calling setupControls";
		controls = baseControls ++ newDrum[\controls]; // WAS an array, NOW an event
		this.setupControls;

		// this Routine creates a new thread
		// not the most robust solution if fade times are extremely short,
		// but much simpler than synchronizing with events from the server.
		Routine {
			server.sync;
			
			"in the performFade routine".postln;
			sourceLast = source;
			// source = newFunction.play(
			source = newDrum[\synth].play(
				outbus:inBus[sourceIndex].index,
				target:group,
				addAction:\addToHead // <- important
			);

			"syncing".postln;
			server.sync;

			"mapping control keys".postln;
			controls.keys.do({ arg key;
				("mapping control key " ++ key).postln;
				source.map(key, controlBus[key]);
			});

			"mapping env params".postln;
			envParams.do({ arg param;
				("mapping env param" ++ param).postln;
				env.map(param, controlBus[param]);
			});
			source.map(\env, envBus.index);

			// TODO: set defaults... maybe?

			if (args.isNil, { args = () });

			controls.keys.do({ arg key;
				if (args.keys.includes(key), {
					controlBus[key].set(args[key]);
				}, {
					controlBus[key].set(controls[key].default);
				});
				controlBus[key].get({ arg x;
					("set control bus " ++ key ++ " to " ++ x).postln;
				});
			});

			// I don't think we need these for drums
			// fadeSynth[sourceIndex].set(\gate, 1);
			// fadeSynth[1-sourceIndex].set(\gate, 0);

			(fadeTime + 0.001).wait;
			this.finishFade;
		}.play;
	}

	finishFade {
		if ((sourceLast== nil).not, {
			sourceLast.free;
		});

		isFading = false;

		if ((sourceQ == nil).not, {
			this.performFade(sourceQ);
			sourceQ = nil;
		});
	}
}
