(
	name: "Whip Snare",
	synth: {
        arg t_trig = 1, vel = 0.3, hz = 180, attack = 0.01, decay = 0.1, curve = 1, heft = 1, slap = 1.5, ffreq = 3000;

        var env, env2, env3, snd1, snd2, sum;

        env = EnvGen.kr(Env.perc(attack, decay, vel, curve), t_trig);

        env2 = EnvGen.kr(Env.perc(attack, decay * heft.max(1), vel, curve), t_trig);
        
        env3 = EnvGen.kr(Env.new([0, hz*slap, hz], [attack * slap, decay * slap.reciprocal.max(1)]), t_trig);

        snd1 = SinOsc.ar(freq: env3, mul: env);

        snd2 = HPF.ar(
            in: WhiteNoise.ar,
            freq: ffreq,
            mul: env2
        );

        sum = snd1 + snd2;
        sum = HPF.ar(sum, 150);
        LPF.ar(sum, 4000) * vel
	},
	controls: (
		attack: ControlSpec(0.0, 0.1, \exp, 0, 0.01, "seconds"),
		decay: ControlSpec(0.05, 2, \exp, 0, 0.1, "seconds"),
		curve: ControlSpec(-20, 20, \lin, 0, 1, "exponent"),
		hz: ControlSpec(20, 10000, \exp, 0, 180, "hz"),
        slap: ControlSpec(0, 20, \exp, 0, 1, "whippiness"),
        heft: ControlSpec(0, 10, \lin, 0, 2, "noise length"),
	)
)