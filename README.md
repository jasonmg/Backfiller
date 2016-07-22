# Backfiller
backfiller mainly doing is fetch data from diverse resource , analysis, filter, and convert to data what we **Need** to save. 
* it provide reliable concurrncy model(which use **AKKA ACTOR**) behind the scene, that developer can only foces on the bussiness unit without concern complicate,various infrastructure details.
* it should be easily expand, that means the framework is quite generic than you thought. ideally/eventually it should be able handle all the case of (fetch -> analysis -> filter/compressor -> sava/sink) process. no mattar where your data come from(DB, file(json,xml,csv ...), etc) or where it save to (DB,file(json,xml,csv ...)).
* TO BE CONTINUE


