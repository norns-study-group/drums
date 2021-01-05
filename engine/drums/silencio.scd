(
    name: "Golden",
    synth: { K2A.ar(0) },
    controls: (
		decay: ControlSpec(0.00001, 120, \exp, default: 1.0),
		curve: ControlSpec(-50, 50, \lin, default: 0),
		attack: ControlSpec(0.00001, 120, \exp, default: 1.0),
    )
)