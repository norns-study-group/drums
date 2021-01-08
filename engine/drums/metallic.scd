(
	name: "Metallic Noise",
	synth: {
	arg hz = 0, vel, env;
	var sig;
	sig = Pulse.ar([
		203.52 + hz,
		301.77 + hz,
		366.31 + hz,
		518.19 + hz,
		538.75 + hz,
		811.16 + hz
	], mul:0.23);
	sig = BPF.ar(sig,TRand.kr(50, 8000, env));
	sig = Splay.ar(sig) * env;
	},
	controls: (
		hz: ControlSpec(0, 2500, \exp, 0, 110, "hz"),
		decay: ControlSpec(0.05, 2, \exp, 0, 0.1, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, 0, "exponent"),
	)
)
