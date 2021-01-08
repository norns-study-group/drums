(
	name: "Metallic Noise",
	synth: {
        arg vel, env;
        var sig;
        sig = Pulse.ar([203.52,301.77,366.31,518.19,538.75,811.16], mul:0.15);
	      sig = Splay.ar(sig) * env;
	},
	controls: (
		decay: ControlSpec(0.05, 2, \exp, 0, 0.1, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, 0, "exponent"),
	)
)
