# Using Neural Networks to Solve the Rubik's Cube

![cube_logo](https://raw.githubusercontent.com/germuth/Personal-Website/master/public/images/main_logo.png)


This repository attempts to solve a generic size rubik's cube with a neural network.

As input to the neural network, we supply each sticker. For example, a 3x3x3 rubik's cube has 6 * 9 = 45 stickers. 
For each sticker, there are 6 possible colors (white, yellow, blue, green, red, orange). Each sticker can be binary encoded like so

white   = 000
yellow  = 001
blue    = 010
green   = 011
red     = 100
orange  = 101

Therefore, for each sticker we need three input neurons which have values of 1 or -1 ( a zero is actually represented as -1). 
So in total, there are 45 stickers and 3 neurons per sticker to get 135 input neurons, at least in the case of a 3x3x3.

The amount of hidden layers and number of neurons in each layer is customizable.

In order to get the neural network to solve the cube, we are going to present to it, the current state of the cube and have
the network attempt to output the correct move, bringing it one step closer to a solution. This is a classification problem.

There are 6 faces to a rubik's cube, and each face can be turned either way. This gives a total of 12 possibilities and can 
be binary encoded to 4 output neuronsin a similar fashion

R   = 0000
R'  = 1000
L   = 0001
L'  = 1001
etc.

Note the move notation used is SiGn. R means turning the right face clockwise. R' means counterclockwise.

Currently this repository uses stochastic back propagation to train the neural network towards low error. The cost function
is the mean squared error between the predicted and actual output.

The neural network performs best on 2x2x2 rubik's cubes. Depending on the depth of the scramble, it may or may not be 
able to solve the cube in entirety. 

This repository also contains the code to evolve the neural network towards lower error with a genetic algorithm, rather than
stochastic back propagation. This code is still experiental and in progress. 

Future work:
---------------------

Change the output layer from a binary encoding to one neuron per move. Although this increases the number of output neurons drastically,
it should simply the decision. Uses a binary encoding may implicitly add an assumption relating outputs to one another.

Test changing the cost function from MSE to a logarithmic equation. This should punish the network more for guessing farther from the truth.



Consider vectorizing Stoachastic Back Propagation to improve performance.
