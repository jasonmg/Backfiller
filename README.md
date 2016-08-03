# Data Acquisition(DAQ)
DAQ mainly doing is fetch data from diverse resource , analysis, filter, and convert to data what we **Need** to save.
* it provide reliable concurrncy model(use __AKKA ACTOR__) behind the scene, that developer can only foces on the bussiness unit without concern complicate,various infrastructure details.
* it should be easily expand, that means the framework is quite generic than you thought. ideally/eventually it should be able handle all the case of (fetch -> load/analysis -> filter/compressor -> sink/insert) process.

# Feature list
* Plugin system that helps developers write their data load and convert process while abstracting technical concerns
* Source system agnostic- can be database, file or whatever system you decides
* Automatic statistics gathering, which help monitoring parallelism, throughput of the system.
* __Strong type__ codebase, which compiler will help catch potential error as earlier as possible. 
