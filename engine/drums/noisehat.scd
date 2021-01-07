(
	name: "Simple Noise Hat",
	synth: {
        arg hz = 9000, vel, env;
        var sig;
       
        sig = WhiteNoise.ar;
        HPF.ar(in: sig, freq: hz, mul: env);
	},
	controls: (
		attack: ControlSpec(0.0, 0.1, \exp, 0, 0.01, "seconds"),
		decay: ControlSpec(0.05, 2, \exp, 0, 0.1, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, 0, "exponent"),
		hz: ControlSpec(20, 10000, \exp, 0, 9000, "hz"),
	)
)