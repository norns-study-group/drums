# drums
january norns study group project

---

Published in the Proceedings of the Symposium on Small Computers and the Arts,
Oct. 1981, IEEE Computer Society Catalog No. 393, pp.19-22.

Manipulations of Musical Patterns
by Laurie Spiegel
September, 1981

A lot of attention in music system design is being given to data entry and score storage formats, timbral synthesis techniques, and to user interface refinements. Such considerations often say more about the problems of computer implementation of music than they do about problems or structures inherent in musical acitivy itself.

I've discussed in other writings the idea that music, which is all too often thought of in terms of tiny sonic entities called "notes," effectively consists of larger configurations. Such musical patterns (including chords, motifs, melodies, rhythms, meters, harmonic progressions, etc. right up to sonatas and symphonies) can be created, described, stored, encoded, orchestrated, and interpreted in a wide variety of ways. The best choice within each of these option fields depends greatly on the nature of the actual material used. This in turn depends on the nature - the structure and function - of the larger musical configuration in progress, and on how the specific materials in question are to fit into it.

That is, the ultimate form we want a composition to take, and the developmental and transformational processes we intend to use in creating it, are important design considerations in developing musical patterns to be used in a piece. (The design of a good fugue subject is a large part of the process of writing a fugue.)

In general, musical patterns of information must be designed to be recurrent, recombinant, and subjectable to selected transformations. (This doesn't mean composing is necessarily a "top down" activity. It's at least as common to come up with a motive and say "How can this be developed into a piece?" as it is to pick a process first, and then ask "Well, now that I've decided to write a fugue, what would make a good subject?")

The process of creating music involves not only the ability to design such patterns of sound, but a working knowledge of all the processes of transformation which can aesthetically be applied to them. Beyond these there needs to be a practised awareness of how such materials and operations, and the specific characteristics of each, relate to and influense each others' potentials. By which I mean both individual processes (e.g. Wed, Sep 1, 1999 transposition) and complex combinations (e.g. the use of transposition as part of a complex of processes such as fugue).

Making a start in this direction, then it seems like a good idea at this stage of computer music's evolution to look at plain old fashioned non-electronic music and to try to extract a basic "library" consisting of the most elemental transformations which have consistently been successfully used on musical patterns, a basic group of "tried-and-true" musical manipulations.

I do this with the following hopes:

1: That such operations may find themselves incorporated into standard compositional tools (programs) of the future, along with such already common text-editor warhorses as insertion, deletion, and global search-and-replace (this last requiring more sophisticated pattern recognition techniques for music than for word processing, of course).

2: That such a roughly drawn initial library, by its content (elemental processes) and structure (recombinable modules) could be of use in developing a more process- (versus entity-) oriented approach to computer music without abandoning principles and practices which have successfully generated music in past eras, as has sometimes happened with process-oriented systems.

3. That this might provide a slightly different model or view which may be useful in increasing our understanding of music, in developing the visual temporal art which is just beginning to evolve, and hopefully also in gaining further insight into human perception (which is, afterall, what music is designed to interface with).

4: That the description of music in terms of a transformationally oriented conceptual vocabulary will help evolve a more appropriate vocabulary and syntax for the description, understanding, and creation of experiences in time, for all self-referential temporal arts, of which music is a very pure example.

On these presumptuous notes of high aspiration, here is a starter's group of basic effe ctive processes of musical pattern manipulation, some minimal modules of transformation, the rules for selection and combination of which, unfortunately, will have to wait.


1. TRANSPOSITION

Adding an offset of fixed magnitude throughout a pattern. (In graphics this operation might be found under the name "translation.") This technique has been most noticably applied to pitch patterns in music, but can be applied to other aspects, such as amplitude, harmonic richness, or tempo. "Parallel" (simultaneous) motion of two musical "voices" (such as in medieval organum) might be described as a pattern of change set in counterpoint to its own transposition.

While repetition and delay (offset) could be described as transposition along the axis of time, these will be dealt with separately here, so as to better reflect how these sub-processes are thought of in music, and to allow the circumstances of their actual use to be better clarified.


2. REVERSAL

Along any axis, as in retrograde (temporal reversal) or inversion (usually along the pitch axis), with no change to the content, order, magnitude or proportion of the pattern's internal structure.

A distinction might be made between positional reversal (of 2 patterns, such as in invertable counterpoint or of antecedent and consequent phrases), and internal reversals, involving uniform change of direction within a single pattern (inversion of a melodic line, where each perfect fifth down becomes a perfect fifth up, etc.).

Reversal can be thought of as implying the concepts of directionality, sequencial order (linearity), and of a center point around which a reversal can occur (a pivot point).

The synchronous reversals of sub-elements or of multiple aspects of retrograded patterns (such as the reversal of the envelopes of notes within a retrograded melodic sequence) might best be described as nested or corrolated (or paired or grouped) reversals, so as to distinguish among, and emphasize the independence of, musical parameters and architectural levels of patterning.


3. ROTATION

Moving something (such as an event, a location counter, one's own position...) from one end of an ordered group to the other end of the same group (in the manner of assembly language rotation instructions), or moving some unique entity through a cyclic entity. What musicians call "inversions" of a chord might better be described as rotations, as they are the movement of a unique discontinuity (an octave offset) through a cyclic group of fixed intervals.


4. PHASE OFFSET

Rotation relative to another cylcic pattern or another instance of the same pattern (for example, a canon or round). Phase can be (somewhat arbitrarily) defined as a relative (or context-dependent) realm of operation, whereas rotation, above, can be considered as an internal (self-contained) transformation (or as though against an absolute).

Different aspects (parameters) of a multi-dimensional theme (a "composite pattern," as long as we're coining terminology) may also "phase" each other. In the medieval isorhythmic motet, the pitch aspect ("color") and the rhythmic aspect ("talea") of a pattern were of different lengths, such that during the course of a piece, one of these aspects would "phase" the other (e.g. the pitch sequence might be three quarters the length of the rhythmic sequence and repeat 4 times while the rhythm would only repeat 3 times) before they end the piece together. Again, this category could be grouped with another (rotation), but is being kept separate here for reasons of musical understanding and usefulness.


5. RESCALING

Expansion or contraction of range of a set of relationships without alteration of the internal proportions. Distances are changed, but not ratios. For example, rhythmic augmentation or diminution, microtonal equal tempe red scales, or playing a rhythmic pattern at a different tempo.

Reversal could be considered as a subprocess of rescaling (by a factor of -1), as scaling is really a form of multiplication. (Again, kept separate here to better reflect traditional musical perspectives.)


6. INTERPOLATION

Filling in between previously established points. Inserting a smooth ramp between discretely separated values, a fast-moving melody added over slow-moving chords, or additional chords put between given chords, embellishing with trills or other such ornamentation. The renaisance practise of "divisions playing" (improvising variations on a theme) was a method of extending shorter patterns into longer compositions by means of melodic interpolation (see also medieval trope and melisma).


7. EXTRAPOLATION

Extension beyond that which already exists in such a way as to preserve continuity with it, to project from it. This enters the realms of good intellect and/or sensative creat ive imagination. What is described as "free evolution" of musical material often consists largely of the performance of this operation on extant patterns.


8. FRAGMENTATION

The isolation, usually for purposes of separate manipulation, of a sub-pattern which has occurred (or "been stated") as a part of a larger configuration. (Haydn and Beethoven may be most famed for this type of "motivic development" but look at the way Bach's fugal episodes use fragments of his long fugue subjects, too.)

Generally, fragmentation has been done along the time axis, as in most examples by the above-cited composers, but it can also be applied through the separation of different parameters of a composite pattern (pitch, duration, articulation, orchestration, etc.), especially with the new conceptual freedom which electronics and mathematics provide (see below).


9. SUBSTITUTION

Of a particular within a group, a pattern within a pattern group, an event in a sequence which is other than what the listener has been led to expect (for example, a classical "deceptive cadence"), of a chord within or a melody against a chord progression, of different instrumentation in a restatement, etc. Substitutions can be made without rule or else by some orderly process, individually, or as part of a group of coordinated operations on material (an "exchange" could be described as a symmetrical or bidirectional substitution).

New technologies no longer isolate parameters of sound (or of image) from each other by method of description. Voltages and numbers represent patterns in far more general ways than staff notation (symbolic representation) or paint (the instance itself). This has made interparametric pattern substitution much easier to explore.

Note that substitution is only apparent if an original version of a pattern (or pattern group) has been sufficiently well established, either through repetition or by its striking design, to be clearly recognizable, and if enough of the original has been preserved after the substitution has been done to make the change noticable.

This brings up the important question of the extent to which the patterning in music must be consciously perceivable for it to provide the experiences humans want from music. (This is not a question with an answer.)


10. COMBINATION

Familiar terms include "mixing" and "overdubbing" and also "counterpoint" and "harmony."

The main unanswerable question for this operation is that of the degree to which each entity which is combined maintains a separately perceivable identity, as opposed to losing that individuality, becoming merged, blended with other component elements into a single unified texture (see gestalt psychology).

It can be speculated that the power of Bach's music rests in large part on his ability to pivot on the balance point between 2 modes of perception, the older parallelistic (po lyphonic, contrapuntal) and the newer group-sequential (homophonic, harmonic) mode. We have a great challenge in the creation of technological tools which could permit us to determine the balance on other perceptual axes as well, to move freely between discrete and merged perception in the domains of figure/ground, harmony/timbre, succession/continuation, et cetera.

If humans could develope sufficient self-understanding, there would be no reason why such high level powerful variablaes as just mentioned could not be available to composers to manipulate directly, replacing myriad weaker ones, focussing the act of composition on a much higher level than is generally practised. This would involve a changeover in the information to be specified by artists, and dealt with via our creative tools, from terminology based on the parameters of art's materials to language designed to reflect the pure processes of thought.


11. SEQUENCING

Couched in such familiar terms as " append" or "splice" and "delete" or "edit out," this is really the termporal dimension of "combination," above. Again, this process is being described separately from its more general form out of deference to musical practise. Hearing, and therefore music, seem to embody greater refinement of sensativity toward transition over time than do the visual sense and its art, which exhibit more refined sensativity toward distinctions and blendings of simultaneities.

Sequencial transitions involve the construction of paths along axes we might define as "disjunct/conjunct/overlapped", or "continuous/discrete." Whenever there is a continuous transition, its rate and curvature are highly expressive musical dimensions.


12. REPETITION

Many powerful musical forms have been based on it (canon, fugue, passacaglia, sonata, rondo, variations, strophic song, etc., etc.).

Important considerations pertinent here involve:

the balance between redundency and new informati on, (see information theory),

the absolute density of new information over time, including how the human ability to absorb a given density changes (see perceptual and/or cognitive psychology),

the use of repetition (listener recognition), versus continuity (listener extrapolation) in creating predictability, in leading the listener to hypothesize, to expect,

how specific material relates to listeners' pattern recognition abilities (including the ability to recognize originals after they've been put through various transformations), and the role of learning in pattern recognition ability (musical style enters here with the question of the types of patterns and manipulations with which listeners have developed the greatest facility),

the use, and composition into music, of the above-referenced ratios, to manipulate (among what other things?) expectation, emotion, physiology, consciousness, and thought.

the role of process-oriented and language-based technologies in exploring such aesthetic questions, and in providing those who wish to create with tools the syntax and variables of which are operant on the levels these questions address.


13. THE GREAT UNKNOWN

The dominant aesthetic transformational processes of the future, which could be to those of the present and past as only their own vocabulary may be able to describe, may more closely approximate or express the complex and delicate processes of the mind than those above.

They may, however, first be begin to be described in the aesthetic languages of technologies we now know.

Laurie Spiegel
New York, 1981

## changelog

- @tyleretters - init project
- @tyleretters - start of a step sequencer
- @Quixotic7- added gridStep for playing drums sounds with grid. 