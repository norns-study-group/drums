(
    name: "Golden",
    synth: { K2A.ar(0) },
    controls: (
        decay: ControlSpec(0.00001, 120, \exp, 0, 1.0, "seconds"),
        curve: ControlSpec(-50, 50, \lin, 0, 0, "exponent"),
        attack: ControlSpec(0.00001, 120, \exp, 0, 1.0, "seconds"),
    )
)