/* TODO: 
	- change monolothic drone code to kit (8 drums?)
	- create drum .sc/.scd format declaring synth as well as param ranges (min/max/default)
	- create external AD envelopes to be consumed by synths
	- allow "hot reloading" of drums
	- (maybe) add SC-based clock source sending OSC back to drum patterns
*/

Drums {
	var <drums;
	var <socket; // a Drum_SynthSocket
	// var inJacks, recordBus, <recorder;
	var amp, hz;

	*new { arg server, baseDrumPath;
		^super.new.init(server, baseDrumPath)
	}

	init { arg server, baseDrumPath;
		if (baseDrumPath == nil, {
			baseDrumPath = PathName(Document.current.path).pathOnly ++ "engine/drums";
		});
		postln("searching for drums at: " ++ baseDrumPath);

		if (drums == nil, {
			drums = Dictionary.new;
		});

		// This could be called later on to "refresh"
		this.scanForNewDrums(baseDrumPath);

		socket = 8.collect{ 
			Drum_SynthSocket.new(server, 0) 
		};

	}

	scanForNewDrums { arg baseDrumPath;
		("scanning for drums in " ++ baseDrumPath).postln;
		PathName.new(baseDrumPath).files.do({|e|
			/* TODO: 
			- fade & reload any sockets w/ new defs (need to track name)
			*/
			var name = e.fileNameWithoutExtension.asSymbol;
			var touchedTime = File.mtime(e.fullPath);

			var drum = drums[name];
			("preparing drum " ++ name).postln;
			try {
				if (drum.isNil.not, {
					var dTouched = drum[\touched];
					("checking for updated drum " ++ name).postln;
					// This has to be nested because SC doesn't bail
					// on false left operand for && 
					// (probably so it works with audio)
					if(dTouched.isNil.not && dTouched < touchedTime, {
						("found updated drum " ++ name ++ ". updating...").postln;
						drums[name] = e.fullPath.load;
						drums[name][\touched] = touchedTime;
					});
				}, {
					("adding brand new drum " ++ name).postln;
					drums[name] = e.fullPath.load;
					drums[name][\touched] = touchedTime;
				});
				("done preparing " ++ name ++ ".").postln;
			} { arg error;
				("error loading drum " ++ name ++ ":").postln;
				error.postln;
			}
		});
		drums.postln;
	}

	start { arg index, name;
		if (drums.keys.includes(name), {
			postln("setting drum #" ++ index ++ " to: " ++ name);
			socket[index].setSource(drums[name]);
			postln("set drum #" ++ index ++ " to: " ++ name);
		}, {
			postln("drums do not know this drum: " ++ name);
			name.class.postln;
			postln("drums do know:");
			drums.keys.postln;
			drums.keys.collect({|k| k.class }).postln;
		});
	}

	setParam { arg index, param, value;
		("setting drum #"++index++" param "++param++"to "++value).postln;
		socket[index].setParam(param, value);
	}

	randomizeParam { arg index, param;
		("randomizing drum #"++index++" param "++param).postln;
		socket[index].randomizeParam(param);
	}

	mapParam { arg index, param, value;
		value = value.max(0).min(1);
		// ("mapping drum #"++index++" param "++param++" value " ++ value ++ "to range").postln;
		socket[index].mapParam(param, value);
	}

	setOneParamLag { arg index, param, time;
		("setting drum #"++index++" param "++param++" lag to "++time).postln;
		socket[index].setOneParamLag(param, time);
	}

	setAllParamLag { arg index, time;
		("setting drum #"++index++" all param lag to "++time).postln;
		socket[index].setAllParamLag(time);
	}

	free {
		socket.do{|s| s.free };
	}
}

// norns glue
Engine_Drums : CroneEngine {
	classvar luaOscPort = 10111;

	var drums; // a Drums
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		var luaOscAddr = NetAddr("localhost", luaOscPort);

		//  :/
		drums = Drums.new(context.server, "/home/we/dust/code/drums/engine/drums" );

		drums.drums.keys.do({ arg name;
			("sending name: " ++ name).postln;
			luaOscAddr.sendMsg("/add_drum", name);
		});

		this.addCommand("pick_synth", "is", { arg msg;
			var index = msg[1];
			var synthName = msg[2];
			("setting drum #"++index++" to "++synthName);
			drums.start(index, synthName);
		});

		this.addCommand("set_param", "isf", { arg msg;
			var index = msg[1];
			var param = msg[2];
			var value = msg[3];
			drums.setParam(index, param, value);
		});

		this.addCommand("randomize_param", "is", { arg msg;
			var index = msg[1];
			var param = msg[2];
			drums.randomizeParam(index, param);
		});

		this.addCommand("map_param", "isf", { arg msg;
			var index = msg[1];
			var param = msg[2];
			var value = msg[3];
			drums.mapParam(index, param, value);
		});

		this.addCommand("set_all_param_lag", "sf", { arg msg;
			var param = msg[1];
			var time = msg[2];
			drums.setAllParamLag(param, time);
		});

		this.addCommand("set_one_param_lag", "isf", { arg msg;
			var index = msg[1];
			var param = msg[2];
			var time = msg[3];
			drums.setOneParamLag(index, param, time);
		});

		this.addCommand("trigger", "i", { arg msg;
			var index = msg[1];
			("triggering drum #"++index);
			drums.socket[index].trig;
		});

		this.addCommand("set_fadetime", "if", { arg msg;
			var index = msg[1];
			var fadeTime = msg[2].asFloat;
			drums.socket[index].setFadeTime(fadeTime);
		});
	}

	free {
		drums.free;
	}
}