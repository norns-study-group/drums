/* TODO: 
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
		// postln("searching for drums at: " ++ baseDrumPath);

		if (drums == nil, {
			drums = Dictionary.new;
		});

		// This could be called later on to "refresh"
		this.scanForNewDrums(baseDrumPath);

		"creating sockets".postln;
		socket = 8.collect{ arg index; 
			Drum_SynthSocket.new(server, 0, index) 
		};
		"created sockets".postln;
	}

	scanForNewDrums { arg baseDrumPath;
		// ("scanning for drums in " ++ baseDrumPath).postln;
		PathName.new(baseDrumPath).files.do({|e|
			/* TODO: 
			- fade & reload any sockets w/ new defs (need to track name)
			*/
			var name = e.fileNameWithoutExtension.asSymbol;
			var touchedTime = File.mtime(e.fullPath);

			var drum = drums[name];
			// ("preparing drum " ++ name).postln;
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
					// ("adding brand new drum " ++ name).postln;
					drums[name] = e.fullPath.load;
					drums[name][\touched] = touchedTime;
				});
				// ("done preparing " ++ name ++ ".").postln;
			} { arg error;
				("error loading drum " ++ name ++ ":").postln;
				error.postln;
			}
		});
		// drums.postln;
	}

	start { arg index, name;
		fork {
			("start " ++ index).postln;
			while { socket[index].ready.not } {
				// ("drum #"++index++" not ready yet. waiting to set voice to "++name).postln;
				0.2.wait;
			};
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
	}

	setParam { arg index, param, value;
		fork {
			// ("set index " ++ index ++ " param " ++ param).postln;
			if (socket[index].ready.not || socket[index].voiceSet.not , {
				while { socket[index].ready.not || socket[index].voiceSet.not } {
					// ("drum #"++index++" not ready yet. waiting to set param "++param++" to "++value).postln;
					1.wait;
				};
				if ((index > 1) && (index < 6), {
					("FINALLY drum #" ++ index ++ " is ready to set " ++ param ++ "to " ++ value ++ "!").postln;
					// socket[index].dump;
				})
			});
			// ("setting drum #"++index++" param "++param++"to "++value).postln;
			socket[index].setParam(param, value);
		};
	}

	randomizeParam { arg index, param;
		// ("randomizing drum #"++index++" param "++param).postln;
		socket[index].randomizeParam(param);
	}

	mapParam { arg index, param, value;
		value = value.max(0).min(1);

		// ("map index " ++ index ++ " param " ++ param).postln;
		fork {
			while { socket[index].ready.not || socket[index].voiceSet.not } {
				// ("drum #"++index++" not ready yet. waiting to set param "++param++" to "++value).postln;
				1.wait;
			};
			// ("mapping drum #"++index++" param "++param++" value " ++ value ++ "to range").postln;
			socket[index].mapParam(param, value);
		};
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

	var <drums; // a Drums
	var <forwards;

	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		var luaOscAddr = NetAddr("localhost", luaOscPort);

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

		this.addCommand("get_param", "is", { arg msg;
			var index = msg[1];
			var name = msg[2];

			luaOscAddr.sendMsg("/param", name, drums.socket[index].getParam(name));
		});

		this.addCommand("trigger", "i", { arg msg;
			var index = msg[1];
			// ("triggering drum #"++index).postln;

			// "postln".postln;

			// ("trigger " ++ index).postln;
			if(drums.socket[index].ready && drums.socket[index].voiceSet, {
				drums.socket[index].trig;
				forwards[index].do({|findex|
					// ("forwarding " ++ index ++ " to " ++ findex).postln;
					drums.socket[findex].trig;
				}) 
			}, { 
				// ("hang on, drum #"++index++" is not ready").postln;
			});
		});

		this.addCommand("add_forward", "ii", { arg msg;
			var index = msg[1];
			var findex = msg[2];

			("setup forward of trig "++index++" to "++findex).postln;

			if ((index >= 0) && (index < 8) && 
				(findex >= 0) && (findex < 8) && 
				(index != findex), 
			{
				forwards[index] = (forwards[index] ++ findex).asSet.asArray;
			});
		});

		this.addCommand("set_fadetime", "if", { arg msg;
			var index = msg[1];
			var fadeTime = msg[2].asFloat;
			drums.socket[index].setFadeTime(fadeTime);
		});

		fork {
			//  :/
			"setting up drums".postln;
			drums = Drums.new(context.server, "/home/we/dust/code/drums/engine/drums" );
			"setingg up forwads".postln;
			forwards = 8.collect({ [] });

			/*
			// idk how valuable this even is
			5.wait;
			"sending drums keys".postln;

			drums.drums.keys.do({ arg name;
				("sending name: " ++ name).postln;
				luaOscAddr.sendMsg("/add_drum", name);
			}); */
		}
	}

	free {
		drums.free;
	}
}