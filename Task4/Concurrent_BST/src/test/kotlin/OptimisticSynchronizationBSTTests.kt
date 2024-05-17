package trees

import org.example.OptimisticSyncBST

class OptimisticSyncBSTTests : GeneralTests<OptimisticSyncBST<Int, Int>>(
    treeType = { OptimisticSyncBST() }
)