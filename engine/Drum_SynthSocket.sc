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
	var <panParams;

	var <ready;
	var <voiceSet;

	var baseControls;

	// var <synthProxy;
	// var <paramProxy;

	var <drum;
	var <env;
	var <pan;
	var <drumBus;
	var <envBus;

	var <controlSynths;
	var <controlBuses;

	var controlLagTime = 0.2;
	var fadeTime = 2.0;
	var index;

	*new {
		arg server,      // instance of Server
		out, i;             // output bus index
		// "SUPER THANKS FOR ASKING".postln;
		^super.new.init(server, out, i);
	}

	init {
		arg s, out, i;
		ready = false;

		"CONSTRUCTIN THINGS".postln;
		server = s;
		index = i;

		envBus = Bus.control(server, 1);
		drumBus = Bus.audio(server, 1);

		voiceSet = false;

		// synthProxy = ProxySpace.new;
		// synthProxy.fadeTime = fadeTime;

		// paramProxy = ProxySpace.new;
		// paramProxy.fadeTime = controlLagTime;

		envParams = [\attack, \decay, \curve];
		panParams = [\pan, \dewey, \system];

		baseControls = (
			pan: ControlSpec(-1.0, 1.0, \lin, 0, 0, "position"),
			decay: ControlSpec(0.05, 5, \exp, 0, 2, "seconds"),
			curve: ControlSpec(-10, 10, \lin, 0, -4, "exponent"),
			hz: ControlSpec(8.18, 13289.75, \exp, 0, 220, "hz"),
			attack: ControlSpec(0.001, 1, \exp, 0, 0.01, "seconds"),
			vel: ControlSpec(0, 1.0, \lin, 0, 1.0, "velocitudes"),
			slap: ControlSpec(0, 1.0, \lin, 0, 1.0, "slap"),
			heft: ControlSpec(0, 1.0, \lin, 0, 1.0, "heft"),
			dewey: ControlSpec(0.01, 48000.0, \lin, 0, 48000.0, "huey lewis"),
			system: ControlSpec(1.0, 24.0, \lin, 0, 24.0, "bit rot"),
		);

		controlBuses = Dictionary.new(baseControls.size);
		controlSynths = Dictionary.new(baseControls.size);
		baseControls.keys.do({ arg key;
			key = key.asSymbol;
			controlBuses[key] = Bus.control(server, 1);
			// server.sync; // is this needed?
			controlSynths[key] = { arg out, value, lagTime=0.01; 
				Out.kr(out, value.lag(lagTime)
				// .poll(2, "" ++ index ++ " " ++ key.asString)
				)
			}.play(outbus: controlBuses[key]);
		});

		"setting up env synth".postln;
		// synthProxy[\env] = 
		env = 
		{ arg out, attack=0, decay=5, curve=(-4), vel=0.999, t_trig=1;
			var envSpec = Env.perc(attack, decay, vel.max(0).min(1), curve: curve);
			Out.kr(out, EnvGen.kr(envSpec, t_trig)
			// .poll(2, "ENV-I-OUS " ++ index)
			)
		}.play(outbus: envBus);

		"setting up dummy drum (a.k.a. drummy)".postln;
		// synthProxy[\drum] = 
		drum = 
		{ WhiteNoise.ar(0.0001) }.play(outbus: drumBus);

		"setting up pan synth".postln;
		// synthProxy[\pan] = 
		pan = 
		{ arg inBus, pan=0, dewey=48000, system=24;
			// Pan2.ar(synthProxy[\drum].ar(1), paramProxy[\pan].kr(1));
			// Pan2.ar(\in.ar(1), paramProxy[\pan].kr(1));
			// insert more processors here
			Pan2.ar(Decimator.ar(In.ar(inBus), dewey, system), pan);
		}.play(outbus: out, args: [\inBus, drumBus]);
		fork {
			// server.sync;

			// synthProxy[\pan].play(out);
		};

		controlSpecs = baseControls;
		fork {
			server.sync;

			this.setControlValues;
			this.mapBase;
			this.mapDrum;

			server.sync;
			ready = true;
		}
		// "mapping stuff.".postln;
	}

	mapBase {
		("mapping env " ++ index ++ " params").postln;
		// synthProxy[\pan].map(\pan, controlSpecs[\pan]);
		envParams.do({ arg key;
			key = key.asSymbol;
			// synthProxy[\env].map(key, paramProxy[key]);
			env.map(key, controlBuses[key].index);
		});
		panParams.do({ arg key;
			key = key.asSymbol;
			// synthProxy[\env].map(key, paramProxy[key]);
			pan.map(key, controlBuses[key].index);
		});
	}

	mapDrum {
		("mapping drum " ++ index ++ " params").postln;
		// "in mapStuff. syncing with server. now to map.".postln;
		// "in mapStuff. synced with server. now to map.".postln;

		// couldn't get these to work so I'm hard-coding it. whatever.
		// synthProxy[\pan] <<>.in synthProxy[\drum]; 
		// synthProxy[\pan].map(\in, synthProxy[\drum]);

		// "mapped env to sytnh. now mapping env controls".postln;
		// envParams.do({ arg param;
		// 	synthProxy[\env].map(param, paramProxy[param]);
		// });


		controlSpecs.keys.do({ arg key;
			key = key.asSymbol;
			drum.map(key, controlBuses[key].index);
		});

		// this one kinda important LMAO
		drum.map(\env, envBus.index);


		// synthProxy[\pan].set(\in, synthProxy[\drum]);
		// "mapped env controls".postln;
		// "mapped stuff.".postln;
	}

	setControlValues {
		// "freeing params".postln;
		// paramProxy.free;
		// "maybe freed params".postln;
		("setting " ++ index ++ " control values").postln;

		controlSpecs.keys.do({ arg key;
			key = key.asSymbol;
			// ("setting "++key++" to "++controlSpecs[key].default).postln;
			// paramProxy[key] = controlSpecs[key].default;
			controlSynths[key].set(\value, controlSpecs[key].default);
		});
		/*
		fork {
			// "syncing before setting velocity fade time".postln; 
			server.sync;
			// "setting velocity fade time to 0".postln; 
			paramProxy[\vel].fadeTime = 0;
		}
		*/

		// "set controls values".postln; 
	}

	setFadeTime { arg time;
		// synthProxy.fadeTime = time;
	}

	trig {
		// "triggering".postln;
		// synthProxy[\env].set(\t_trig, 1);
		// synthProxy[\drum].set(\t_trig, 1);
		// if(index == 0, {
		// 	("env node ID:" ++ synthProxy[\env].nodeID).postln;
		// 	("drum node ID:" ++ synthProxy[\drum].nodeID).postln;
		// })
		env.set(\t_trig, 1);
		drum.set(\t_trig, 1);
	}

	setParam { arg key, value;
		// paramProxy[key] = value;
		controlSynths[key.asSymbol].set(\value, value);
	}

	getParam { arg key;
		// var value = paramProxy[key].getKeysValues()[0][1];
		// ("getParam returning " ++ key ++ " = " ++ value).postln;
		// ^value;
		"NO PARAM VALUE FOR YOU".postln;
	}

	randomizeParam { arg key;
		// key.class.postln;
		// controlSpecs.keys.asArray[0].class.postln;
		var value = controlSpecs[key].map(1.0.rand);
		// ("new random " ++ key ++ " value: "++ value).postln;
		// paramProxy[key] = value;
		this.setParam(key, value);
	}

	mapParam { arg key, normalized;
		var value; 
		// ("new " ++ key ++ " input value for mapping: "++ value).postln;
		value = controlSpecs[key].map(normalized);
		// ("new " ++ key ++ " value: "++ value).postln;
		// paramProxy[key] = value;
		this.setParam(key, value);
	}

	setOneParamLag { arg key, time;
		// paramProxy[key].fadeTime = time;
		controlSynths[key.asSymbol].set(\lagTime, time);
	}

	setAllParamLag { arg time;
		// paramProxy.fadeTime = time;
		controlSynths.do({ arg controlSynth;
			controlSynth.set(\lagTime, time);
		})
	}

	free {
		// synthProxy.end(0);
		// paramProxy.end(0);
		controlSynths.do({|c| c.free });
		env.free;
		drum.free;
		pan.free;

		controlBuses.do({|c| c.free });
		envBus.free;
		drumBus.free;
	}

	//////////////////////////////////////////
	/// private

	// performFade { arg newFunction, args;
	setSource { arg newDrum, args;
		("setting " ++ index ++ " source").postln;
		voiceSet = false;
		// "performing fade".postln;
		// newDrum.postln;

		// "performFade calling setControlValues";
		controlSpecs = baseControls++newDrum[\controls]; // WAS an array, NOW an event
		this.setControlValues;

		// this Routine creates a new thread
		// not the most robust solution if fade times are extremely short,
		// but much simpler than synchronizing with events from the server.
		fork {
			// synthProxy[\drum] = newDrum[\synth];
			("BE FREE DRUM # " ++ index ++ " !!!").postln;
			drum.free; // stop IMMEDIATELY AND POP LOUDLY AND RUDELY
			("WELCOME NEW DRUM # " ++ index ++ " !!!").postln;
			drum = newDrum[\synth].play(outbus: drumBus);

			// "syncing".postln;
			("sync DRUM # " ++ index ++ " !!!").postln;
			server.sync;
			("sync DRUM # " ++ index ++ " !!!").postln;

			this.mapDrum;

			// "mapping control keys".postln;
			// controlSpecs.keys.do({ arg key;
			// 	// ("mapping control key "++key).postln;
			// 	synthProxy[\drum].map(key, paramProxy[key]);
			// });

			// "mapping env params".postln;
			// envParams.do({ arg param;
			// 	// ("mapping env param"++param).postln;
			// 	synthProxy[\env].map(param, paramProxy[param]);
			// });

			// "donemapping env params".postln;
			// synthProxy[\drum].map(\env, synthProxy[\env]);

			// if (args.isNil, { args = () });

			// controlSpecs.keys.do({ arg key;
			// 	if (args.keys.includes(key), {
			// 		paramProxy[key] = args[key];
			// 		paramProxy[key].get({ arg x;
			// 			("set param "++key++" to "++x++" (via args)").postln;
			// 		});
			// 	});
			// });
			server.sync;
			voiceSet = true;
			("done setting params for " ++ index).postln;
		};
	}
}
