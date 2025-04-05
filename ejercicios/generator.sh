#!/bin/bash

# Verificar que se haya proporcionado el número de conexiones concurrentes
if [ -z "$1" ]; then
    echo "Uso: ./generator.sh <conexiones_concurrentes>"
    exit 1
fi

concurrent_requests=$1
total_time=0

for i in {1..5}; do
    result=$(ab -n 1000 -c "$concurrent_requests" http://localhost:8080/status 2>&1 | grep 'Time taken for tests' | awk '{print $5}')
    
    # Verifica si el resultado es vacío o no numérico
    if ! [[ "$result" =~ ^[0-9]+(\.[0-9]+)?$ ]]; then
        echo "Error en la ejecución $i: no se pudo obtener el tiempo"
        continue
    fi

    echo "Ejecución $i: $result segundos"
    total_time=$(echo "$total_time + $result" | bc)
done

# Calcular el promedio solo si hubo datos válidos
if [ "$total_time" != "0" ]; then
    average=$(echo "scale=2; $total_time / 5" | bc)
    echo "Promedio: $average segundos"
else
    echo "No se obtuvieron resultados válidos para calcular el promedio."
fi
