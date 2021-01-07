(
	name: "Smooth Brain",
	synth: {
		arg hz, vel, envMod, env=0, hissyFit;
		Mix.ar([
			WhiteNoise.ar(1 - env) * hissyFit,
			SinOsc.ar(
				freq: ((env * envMod * 12) + hz.cpsmidi).midicps, 
				mul: env * vel
			)
		]);
	},
	controls: (
		decay: ControlSpec(0.05, 2, \exp, 0, 0.8, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, -6, "exponent"),
		hz: ControlSpec(20, 320, \exp, 0, 35, "hz"),
		envMod: ControlSpec(0, 12, \lin, 0, 2.3, "octaves"),
		hissyFit: ControlSpec(0, 1, \lin, 0, 0.005, "floors"),
		attack: ControlSpec(0.0, 0.1, \exp, 0, 0.0, "seconds"),
	)
)