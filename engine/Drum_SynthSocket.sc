/* TODO:
- Make a mixer - this thing's gonna clip
- Add another out for the sidechain
- Make a mix bus for sidechaining
- Randomize a param
- Randomize all params
- Set a param to default 
- Set all params to default 
- Effects?
- LFOs?
*/

// original:

// a primitive and limited analog to `NodeProxy`,
// which just crossfades between two stereo synth functions.
// motivation is to limit the number of active synths during crossfades,
// by specifying a shallow queuing behavior.
// @zebra

// (copied from Dronecaster and HEAVILY EDITED by @license)

Drum_SynthSocket {
	var server;          // a Server!
	var controlSpecs;        // Set of control names
	var <envParams;

	var baseControls;

	var synthProxy;
	var paramProxy;

	var controlLagTime = 0.2;
	var fadeTime = 2.0;

	*new {
		arg server,      // instance of Server
		out;             // output bus index
		^super.new.init(server, out);
	}

	init {
		arg s, out;
		server = s;

		synthProxy = ProxySpace.new;
		synthProxy.fadeTime = fadeTime;

		paramProxy = ProxySpace.new;
		paramProxy.fadeTime = controlLagTime;

		envParams = [\attack, \decay, \curve];

		baseControls = (
			pan: ControlSpec(-1, 1, \lin, 0, 0, "position"),
			decay: ControlSpec(0.05, 5, \exp, 0, 2, "seconds"),
			curve: ControlSpec(-10, 10, \lin, 0, -4, "exponent"),
			hz: ControlSpec(8.18, 13289.75, \exp, 0, 220, "hz"),
			attack: ControlSpec(0.001, 1, \exp, 0, 0.01, "seconds"),
		);

		"setting up env synth".postln;
		// Don't need a new one for each drum. Just reuse forever
		// This trigBus thing is really dumb. Maybe there's a less crappy way to do this.
		// Worst-case, could utilize the velocity bus. I forgot to add that anyway :)
		synthProxy[\env] = { arg bus, attack=0, decay=0.5, curve=(-4), t_trig=0;
			var envSpec = Env.perc(attack, decay, 1, curve: curve);
			EnvGen.kr(envSpec, t_trig);
		};


		"setting up dummy drum (a.k.a. drummy)".postln;
		synthProxy[\drum] = { WhiteNoise.ar(0.0001) };

		"setting up pan synth".postln;
		synthProxy[\pan] = {
			Pan2.ar(synthProxy[\drum], paramProxy[\pan]);
		};
		synthProxy[\pan].play(out);

		"setting up controls ".postln;
		controlSpecs = baseControls;
		this.setupControls;
		"mapping stuff.".postln;
		this.mapStuff;
	}

	mapStuff {
		Routine {
			server.sync;
			"in mapStuff. going to sync with server.".postln;
			"in mapStuff. synced with server. now to map.".postln;

			// couldn't get these to work so I'm hard-coding it. whatever.
			// synthProxy[\pan] <<>.in synthProxy[\drum]; 
			// synthProxy[\pan].map(\in, synthProxy[\drum]);

			synthProxy[\pan].map(\pan, controlSpecs[\pan]);
			controlSpecs.keys.do({ arg key;
				synthProxy[\env].map(key, paramProxy[key]);
			});
			"mapped env to sytnh. now mapping env controls".postln;
			envParams.do({ arg param;
				synthProxy[\env].map(param, paramProxy[param]);
			});
			"mapped env controls".postln;
			"in mapStuff. mapped stuff.".postln;
		}.play;
	}

	setupControls {
		"freeing params".postln;
		paramProxy.free;
		"maybe freed params".postln;

		controlSpecs.keys.do({ arg key;
			("setting "++key++" to "++controlSpecs[key].default).postln;
			paramProxy[key] = controlSpecs[key].default;
		});

		"set controls values".postln; 
	}

	setFadeTime { arg time;
		synthProxy.fadeTime = time;
	}

	trig {
		synthProxy[\env].set(\t_trig, 1);
	}

	setParam { arg key, value;
		paramProxy[key] = value;
	}

	setOneParamLag { arg key, time;
		paramProxy[key].fadeTime = time;
	}

	setAllParamLag { arg time;
		paramProxy.fadeTime = time;
	}

	free {
		synthProxy.fadeTime = 0.2;
		synthProxy.free;

		paramProxy.fadeTime = 0.2;
		paramProxy.free;
	}

	//////////////////////////////////////////
	/// private

	// performFade { arg newFunction, args;
	setSource { arg newDrum, args;
		"performing fade".postln;
		newDrum.postln;

		"performFade calling setupControls";
		controlSpecs = baseControls++newDrum[\controls]; // WAS an array, NOW an event
		this.setupControls;

		// this Routine creates a new thread
		// not the most robust solution if fade times are extremely short,
		// but much simpler than synchronizing with events from the server.
		Routine {
			server.sync;
			
			synthProxy[\drum] = newDrum[\synth];

			"syncing".postln;
			server.sync;

			"mapping control keys".postln;
			controlSpecs.keys.do({ arg key;
				("mapping control key "++key).postln;
				synthProxy[\drum].map(key, paramProxy[key]);
			});

			"mapping env params".postln;
			envParams.do({ arg param;
				("mapping env param"++param).postln;
				synthProxy[\env].map(param, paramProxy[param]);
			});

			"donemapping env params".postln;
			synthProxy[\drum].map(\env, synthProxy[\env]);

			if (args.isNil, { args = () });

			"setting params to incoming args".postln;
			controlSpecs.keys.do({ arg key;
				if (args.keys.includes(key), {
					paramProxy[key] = args[key];
					paramProxy[key].get({ arg x;
						("set param "++key++" to "++x++" (via args)").postln;
					});
				});
			});
			"done setting params".postln;
		}.play;
	}
}
