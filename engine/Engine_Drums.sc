// "kernel" class, norns-agnostic
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

		this.scanForNewDrums(baseDrumPath);

		socket = 8.collect{ 
			Drum_SynthSocket.new(server, 0, [\amp, \hz, \decay, \wild]) 
		};

		// recordBus = Bus.audio(server, 2);
		// inJacks = { Out.ar(recordBus, SoundIn.ar([0, 1])) }.play;
		// recorder = Recorder.new(server);
	}

	scanForNewDrums { arg baseDrumPath;
		PathName.new(baseDrumPath).files.do({|e|
			/* TODO: 
			- fade & reload any sockets w/ new defs (need to track name)
			*/
			var drum = drums[name];
			var touchedTime = File.mtime(e.fullPath);
			var drumExists = drum.isNil.not && ;
			if (drum.isNil.not, {
				var dTouched = drum[\touched];
				if(dTouched.isNil.not && dTouched != touchedTime), {
					var name = e.fileNameWithoutExtension;
					drums[name] = e.fullPath.load;
					drums[name][\touched] = touchedTime;
				}
			})
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
		});
	}

	setParam({
		arg index, param, value;
		socket[index].setControl(param.asSymbol, value);
	});

	/*setAmp { |index,  arg value;
		amp = value;
		socket.setControl(\amp, amp);
	}

	setHz { arg value;
		hz = value;
		socket.setControl(\hz, hz);
	}*/

	/*stop {
		socket.stop;
	}

	record { arg path;
		recorder.record(path.asString, recordBus, 2);
	}*/

	free {
		// inJacks.free;
		// recorder.free;
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
			var synth = msg[2];
			drums.start(index, name);
		})

		this.addCommand("set_param", "isf", { arg msg;
			var index = msg[1];
			var param = msg[2];
			var value = msg[3];
			// if param is attack or decay, need to set on env
			// not sure whether to do here or elsewhere
			drums.socket[index].setFadeTime(fadeTime);
		});

		this.addCommand("set_param_lag", "isf", { arg msg;
			var index = msg[1];
			var param = msg[2];
			var lag = msg[3];
			drums.socket[index].setFadeTime(fadeTime);
		});

		this.addCommand("trigger", "i", { arg msg;
			var index = msg[1];
			// this is where the env should get triggered
			// not sure whether to do here or elsewhere
			// drums.socket[index].setFadeTime(fadeTime);
		});

		/*this.addCommand("hz", "f", { arg msg;
			drums.setHz(msg[1].asFloat);
		});

		this.addCommand("amp", "f", { arg msg;
			drums.setAmp(msg[1].asFloat);
		});*/

		this.addCommand("set_fadetime", "if", { arg msg;
			var index = msg[1];
			var fadeTime = msg[2].asFloat;
			drums.socket[index].setFadeTime(fadeTime);
		});

		/*this.addCommand("stop", "i", { arg msg;
			drums.stop(msg[1]);
		});

		this.addCommand("start", "s", { arg msg;
			drums.start(msg[1].asString);
		});

		this.addCommand("record_start", "s", { arg msg;
			drums.record(msg[1]);
		});

		this.addCommand("record_stop", "i", { arg msg;
			drums.recorder.stopRecording;
		});*/
	}

	free {
		drums.free;
	}
}