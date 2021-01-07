(
	name: "Slappin' Feedback Kick-o-Rama",
	synth: {
		arg env=0, hz, vel, fbScale;
		var freq = env ** 5 * 7 + 1 * hz;
		// var osc = SinOscFB.ar(freq, env ** fbScale * (vel ** 0.6) * 50);
		var osc = SinOscFB.ar(freq, env ** fbScale * vel);
		(env ** 10 * 5 + 1 * osc).tanh * env * vel;
	},
	controls: (
		decay: ControlSpec(0.05, 2, \exp, 0, 1.2, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, -7, "exponent"),
		hz: ControlSpec(20, 320, \exp, 0, 35, "hz"),
		fbScale: ControlSpec(0, 20, \exp, 0, 7, "smoothness"),
		// I don't think most synths need attack, just adding as an example
		attack: ControlSpec(0.0, 0.1, \exp, 0, 0.0, "seconds"),
	)
)