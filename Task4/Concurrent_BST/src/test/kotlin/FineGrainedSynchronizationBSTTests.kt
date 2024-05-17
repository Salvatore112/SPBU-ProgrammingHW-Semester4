package trees

import org.example.FineGrainedSynchronizationBST

class FineGrainedSynchronizationBST : GeneralTests<FineGrainedSynchronizationBST<Int, Int>>(
    treeType = { FineGrainedSynchronizationBST() }
)