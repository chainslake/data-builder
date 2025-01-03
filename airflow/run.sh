export AIRFLOW_HOME=/home/hadoop/projects/chainslake/airflow
rm airflow-webserver.pid
rm -rf ./logs
airflow standalone
