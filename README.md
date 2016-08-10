# Data Acquisition(DAQ)
DAQ mainly doing is fetch data from diverse resource , analysis, filter, and convert to data what we **Need** to save.
* it provide reliable concurrncy model(use __AKKA ACTOR__) behind the scene, that developer can only foces on the bussiness unit without concern complicate,various infrastructure details.
* it should be easily expand, that means the framework is quite generic than you thought. ideally/eventually it should be able handle all the case of (fetch -> load/analysis -> filter/compressor -> sink/insert) process.

# Feature list
* Plugin system that helps developers write their data load and convert process while abstracting technical concerns
* Source system agnostic- can be database, file or whatever system you decides
* Automatic statistics gathering, which help monitoring parallelism, throughput of the system.
* __Strong type__ codebase, which compiler will help catch potential error as earlier as possible. 


# Statistic output
```
phase   before after failure MeanRate           OneMinuteRate FiveMinuteRate FifteenMinuteRate Max     Mean    Min     75thPercentile 95thPercentile 99thPercentile
======= ====== ===== ======= ================== ============= ============== ================= ======= ======= ======= ============== ============== ==============
slice   3      3     0       0.1910302038290823 0.0           0.0            0.0               5051 ms 5051 ms 5051 ms 5051 ms        5051 ms        5051 ms       
source  3      3     0       0.5734798723384484 0.0           0.0            0.0               13 ms   4 ms    0 ms    13 ms          13 ms          13 ms         
filter  5      2     0       0.5734545521725829 0.0           0.0            0.0               3 ms    1 ms    0 ms    3 ms           3 ms           3 ms          
convert 3      3     0       0.5734343915187865 0.0           0.0            0.0               7 ms    2 ms    0 ms    7 ms           7 ms           7 ms          
sink    2      2     0       0.1911368897587238 0.0           0.0            0.0               7 ms    7 ms    7 ms    7 ms           7 ms           7 ms          
```
