(
	name: "Envelope Trigger test",
	synth: {
		arg hz, vel, envMod, env=0, hissyFit, decay, t_trig;
		Mix.ar([
			WhiteNoise.ar(EnvGen.kr(
                Env.perc(decay / 2, decay / 2, vel * 0.1), 
                t_trig
            )),
			SinOsc.ar(
				freq: hz, 
				mul: env * vel
			)
		]);
	},
	controls: (
		decay: ControlSpec(0.05, 2, \exp, 0, 0.8, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, -6, "exponent"),
		hz: ControlSpec(22, 550, \exp, 0, 110, "hz"),
		attack: ControlSpec(0.0, 0.1, \exp, 0, 0.0, "seconds"),
	)
)