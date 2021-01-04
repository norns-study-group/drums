(
	name: "Slappin' Feedback Kick-o-Rama",
	synth: {
		arg env=0, hz, vel;
		var fbScale = (1 - vel) ** 0.2 * 500;
		var freq = kikEnv ** 5 * 7 + 1 * hz;
		var osc = SinOscFB.ar(freq, kikEnv ** fbScale * (vel ** 0.6) * 50);
		(kikEnv ** 10 * 5 + 1 * osc).tanh;
	},
	controls: (
		decay: ControlSpec(0.05, 2, \exp, default: 0.5),
		curve: ControlSpec(-20, 20, \lin, default: -4),
		hz: ControlSpec(20, 320, \exp, default: 35),
		// I don't think most synths need attack, just adding as an example
		attack: ControlSpec(0.0, 0.1, \exp, default: 0.0),
		// vel should always be 0.0-1.0
	)
)