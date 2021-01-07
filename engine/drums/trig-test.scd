(
	name: "Envelope Trigger test",
	synth: {
		arg hz, vel, env=0, decay, t_trig, heft, slap;
		Mix.ar([
			WhiteNoise.ar(EnvGen.kr(
                Env.perc(decay / 2, decay / 2, vel * 0.2 * heft), 
                t_trig
            )),
			SinOscFB.ar(
				env ** 3 * 5 * slap + 1 * hz, 
                env ** 3 * slap * 3,
				env * vel
			)
		]);
	},
	controls: (
		decay: ControlSpec(0.05, 2, \exp, 0, 0.8, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, -6, "exponent"),
		heft: ControlSpec(0, 1, \lin, 0, 0.1, "exponent"),
		hz: ControlSpec(22, 550, \exp, 0, 110, "hz"),
		attack: ControlSpec(0.0, 0.1, \exp, 0, 0.0, "seconds"),
	)
)