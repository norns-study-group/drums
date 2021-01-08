(
	name: "Smooth Brain",
	synth: {
		arg hz, vel, slap, env=0, heft;
		Mix.ar([
			WhiteNoise.ar(1 - env) * heft,
			SinOsc.ar(
				freq: ((env * slap * 12) + hz.cpsmidi).midicps, 
				mul: env * vel
			)
		]);
	},
	controls: (
		decay: ControlSpec(0.05, 2, \exp, 0, 0.8, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, -6, "exponent"),
		hz: ControlSpec(20, 10000, \exp, 0, 35, "hz"),
		slap: ControlSpec(0, 12, \lin, 0, 2.3, "octaves"),
		heft: ControlSpec(0, 1, \lin, 0, 0.005, "hissy fits"),
		attack: ControlSpec(0.0, 0.1, \exp, 0, 0.0, "seconds"),
	)
)