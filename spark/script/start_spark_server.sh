#!/bin/bash
/lib/spark/sbin/start-thriftserver.sh --master local[2] --deploy-mode client \
    --driver-memory 2g \
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
    --conf spark.databricks.delta.retentionDurationCheck.enabled=false