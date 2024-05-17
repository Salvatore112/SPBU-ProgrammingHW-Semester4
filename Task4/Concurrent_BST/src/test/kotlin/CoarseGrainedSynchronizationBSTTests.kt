package trees

import org.example.CoarseGrainedSynchronizationBST

class CoarseGrainedSynchronizationBSTTests : GeneralTests<CoarseGrainedSynchronizationBST<Int, Int>>(
    treeType = { CoarseGrainedSynchronizationBST() }
)