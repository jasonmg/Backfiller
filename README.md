# Data Acquisition(DAQ)
DAQ basic doing is fetch data from diverse resource, analysis, filter, and convert to data what we **Need** to save.
* it provide reliable concurrncy model(use __AKKA ACTOR__) behind the scene, that developer can only foces on the bussiness unit without concern complicate,various infrastructure details.
* it should be easily expand, which means the framework is quite generic than you thought. ideally/eventually it should be able handle all the case of (fetch -> load/analysis -> filter/compressor -> sink/insert) process.

# Feature list
* Plugin system that helps developers write their data load and convert process while abstracting technical concerns
* Source system agnostic- can be database, file or whatever system you decides
* Automatic statistics gathering, which help monitoring parallelism, throughput of the system.
* __Strong type__ codebase, which compiler will help catch potential error as earlier as possible. 

# Statistic output 
> this is for monitor each acotr phase personality/performance.
> looking for how Metic works/means, please step into [here](http://metrics.dropwizard.io/3.1.0/getting-started/)

```
>>>>>>>>>>>>>>>  elapsed so far: 5179 ms
phase   before after failure MeanRate            OneMinuteRate FiveMinuteRate FifteenMinuteRate Max     Mean    Min     75thPercentile 95thPercentile 98thPercentile 99thPercentile
======= ====== ===== ======= =================== ============= ============== ================= ======= ======= ======= ============== ============== ============== ==============
slice   3      3     0       0.19223376219716182 0.0           0.0            0.0               5038 ms 5038 ms 5038 ms 5038 ms        5038 ms        5038 ms        5038 ms       
source  3      3     0       0.576661314715934   0.0           0.0            0.0               7 ms    2 ms    0 ms    7 ms           7 ms           7 ms           7 ms          
filter  5      2     0       0.576648845354604   0.0           0.0            0.0               13 ms   4 ms    0 ms    13 ms          13 ms          13 ms          13 ms         
convert 3      3     0       0.5766436776471278  0.0           0.0            0.0               6 ms    2 ms    0 ms    6 ms           6 ms           6 ms           6 ms          
sink    2      2     0       0.19221911068113465 0.0           0.0            0.0               7 ms    7 ms    7 ms    7 ms           7 ms           7 ms           7 ms                 
```

# Architecture

## BackfillerSystem
![BackfillerSystem](/architecture/BackfillerSystem.png)

## BackfillerCoreSystem
![BackfillerCoreSystem](/architecture/BackfillerCoreSystem.png)

## ActorSystem
![ActorSystem](/architecture/ActorSystem.png)

## ActorMessageFlow
![ActorMessageFlow](/architecture/ActorMessageFlow.png)

## ActorRepairLifeCycle
![ActorRepairLifeCycle](/architecture/ActorLifeCycle.png)