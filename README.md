# CacheSimulator
The simple cache simulator on a single CPU with L1 and L2 cache architecture

L1 cache is divided into two parts, the instruction cache and data cache.
Instruction cache do not need to support write operation
Data cache is write back. In addition, data cache has a 14 block victim cache, where serves a buffer before really kick the block off.

L2 cache is a unified cache with write-through feature
