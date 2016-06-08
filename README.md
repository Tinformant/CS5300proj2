# CS 5300 Project 2
CS 5300 Project 2 - Map Reduce and Page Rank Algorithms

## Preprocessing

We chose to pre process our blocks.txt and edges.txt into a single file, blocks_edges.txt. ```Parser.java``` is the main program reponsible for this. The formatting for the newly created file looks like this on each line - 

```350  0.00009999000099990002, 51, 1_2_3```

Where 350 is a node from edges.txt, 0.000.. is inital Page Rank weight, 51 is the number of out going edges and 1_2_3... are the out going edges.


For our work, the netid we used was Bjg226 or based on parameter: .622. We took all the edges with value between .9*.622 and .622

## Simple PageRank Algorithm

**1.  Node Driver Class**
1. Class Name: singleNodeMaster.java
    * Roles:
        1. Hard codes the global variables used for the instance
        2. global residual error tracker
            

**2. Mapper Class**
1. Class Name: singleMapper.java
2. receives initial data stream from node drive
3. Passes to Reducer two types of data emissions
    *  node self value -> [node#, "pR PAGE_RANK DEGREE EDGE_LIST"]
    * rank portion distribution -> [receivingNode]
4. Role
    * transforms information from storage (input.txt and intermediary text files) into
        1. a usable framework for analysis
        2. writes to context two types of information data
            * <Node, pageRankValue, degree, edgelist> it tells the reducer the corresponding node's old pagerank value
            * <Node, pageRankPortion> it tells the reducer the corresponding node's contribution given by another node that points to it
        3. Essentially the mapper provides two roles, it tells the reducer the corresponding node's old pagerank value and also tells 
        
**3. Reducer Class**
1. Class Name : singleReducer
    * Role:
        1. Collects all the map items and groups them by the key node
            in summarization, increments the global residual variable
        2. for each key node, reduce does the following:
            * Collects and summarizes all the contributions from the other nodes into pRankPortions variable
            * Collects information about the corresponding node's old page rank as well as its degree and edgelist information




## Blocked PageRank Algorithm

**Key is identified and associated with blocks Each block handles a chunk of nodes**

**1. Node Driver Class**
* Class Name: blockNodeMaster.java
    * Role:
        1. Has a global residual tracker, there will only be 68 incrementations made by each block each incremention represents the average residual experiened by each block and the residual will then by divided by 68 to average the average residuals received by each block
        2. Stores intermediary information within a temporary folder 

**2. Mapper Class**
* Class Name: blockMapper.java
    1. receives initial data stream from node drive
    2. Passes to reduce two types of data emissions
        * Identity token for a node in blockKey
            > <"blockId", "B_ID NodeKey oldPR deg eList">

        * A portion token telling the reducer to pass along the portions to each of the nodes in the list of nodes receiving the portion
            > <"blockId", "B_Por prDist elist"> This step comes in two types

            * Either it is an "EB_Por" or an internal distribution, we create a temporary storage. As we do internal page rank iterations that keep the external distributions constant for a set amount of iterations, we update the internal pagerank distribution.
            * Or it is coming from the boundary in which case we store aggregate and set as constant these distributions
    4. This is done by first getting retrieving all the old information from the initial files
        
**3. Reducer Class**
1. accepts emissions from Mapper Class rather than collecting on individual nodes as keys, we collect keys based on block numbers in doing so we create three types of map emissions
* Self Identification Post
* Portion distribution
    1. within block portion distribution (both nodes are in block)
    2. edge portion portion distribution (nodes differ in blocks, in which case we only care about where the block is being sent.)
2. Creates a memory portion for the incoming portions from external nodes and keeps that constant and also creates a seperate storage for portions coming from within.
3. Iteratively loops until the internal pagerank nodes converge. 
    1. The proportion distributions coming from out-of-block nodes are kept constant at each iteration
    2. The proportion distributions coming from in-the-block nodes are dynamic and changing and redistributed with each iteration



## Running the Application
Run the nodeDriverClass with 2 arguments input.txt and the output location
    a new directory will be made called the output and will contain an internal tmp folder that houses all the residual and pagerank information
Also
 a text file will be printed locally containing the residual information.


the input file is called edges_blocks.txt, the file must be public
the output path is an empty folder in the s3bucket


ARGUMENTS EXAMPLE FOR ELASTIC BEANSTALK

<!-- SINGLE -->
Generic:
proj2.singleNodeMaster <s3bucket_containing//edges_blocks.txt> "output directory"

proj2.singleNodeMaster s3://edu-cornell-cs-cs5300s16-rs2357-proj2/edges_blocks.txt s3://edu-cornell-cs-cs5300s16-rs2357-proj2/single
<!-- BLOCK -->
proj2.blockNodeMaster <s3bucket_containing//edges_blocks.txt> "output directory"

proj2.blockNodeMaster s3://edu-cornell-cs-cs5300s16-rs2357-proj2/edges_blocks.txt s3://edu-cornell-cs-cs5300s16-rs2357-proj2/block

NOTE: the output directory must be empty
to see the average residuals, see the stdout log files of the step
to see the individual page ranks, refer to the output path and look at the last iterative step.

Single Node Residuals

Average Residual at iteration 0 is: 0.5016
Average Residual at iteration 1 is: 0.1712
Average Residual at iteration 2 is: 0.1052
Average Residual at iteration 3 is: 0.0715
Average Residual at iteration 4 is: 0.0506
Average Residual at iteration 5 is: 0.0379


Block Node Residuals - found in stdout
At Iteration 0, the blocks took an average of 21.88iterations.
Average Residual at iteration 0is:0.6508
At Iteration 1, the blocks took an average of 21.68iterations.
Average Residual at iteration 1is:0.0065
At Iteration 2, the blocks took an average of 21.66iterations.
Average Residual at iteration 2is:0.0006
At Iteration 3, the blocks took an average of 21.68iterations.
Average Residual at iteration 3is:0.0001
At Iteration 4, the blocks took an average of 21.66iterations.
Average Residual at iteration 4is:0.0000

