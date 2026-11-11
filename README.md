# Sentiment App — ИИ-приложение для анализа тональности в Kubernetes

Учебный проект по дисциплине «Оркестрация и контейнеризация». Spring Boot-приложение с mock-анализом тональности текста, развёрнутое в локальном кластере Minikube с мониторингом Prometheus + Grafana.

## Стек технологий

- Java 17 (Spring Boot 4.1.2)
- Docker — контейнеризация (multi-stage build, образ 90.2 МБ)
- Kubernetes (Minikube, 2 узла, 4 CPU, 4096 МБ)
- Prometheus + Grafana (kube-prometheus-stack)
- Helm — установка мониторинга

## Сборка и запуск

### 1. Сборка JAR

./mvnw clean package -DskipTests

### 2. Сборка Docker-образа

docker build -t sentiment-app:1.0 .

### 3. Запуск Minikube

minikube start --driver=docker --cpus=4 --memory=4096mb --nodes=2
minikube addons enable metrics-server
minikube addons enable ingress

### 4. Загрузка образа в Minikube

minikube image load sentiment-app:1.0

### 5. Развёртывание в Kubernetes

kubectl apply -f k8s/
kubectl get pods

### 6. Доступ к приложению

minikube tunnel
curl "http://localhost/api/sentiment?text=I+love+this"

Ожидаемый ответ: {"sentiment":"positive","text":"I love this"}.

## Мониторинг

### Установка стека

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
kubectl create namespace monitoring
helm install kube-prom prometheus-community/kube-prometheus-stack -n monitoring

### Доступ к Prometheus

kubectl port-forward -n monitoring svc/kube-prom-kube-prometheus-prometheus 9090:9090

Открыть: http://localhost:9090/targets (приложение должно быть в статусе UP).

### Доступ к Grafana

kubectl port-forward -n monitoring svc/kube-prom-grafana 3000:80

Открыть: http://localhost:3000 (логин admin, пароль — из секрета kube-prom-grafana).

Импортировать дашборд ID 4701 (JVM Micrometer).

## API

| Метод | Путь | Параметр | Ответ |
|-------|------|----------|-------|
| GET | /api/sentiment | text | {"text":"...","sentiment":"positive|negative|neutral"} |
| GET | /actuator/health | — | Состояние приложения |
| GET | /actuator/prometheus | — | Метрики для Prometheus |

## Результаты

- Образ: 90.2 МБ (менее 150 МБ).
- Приложение: 3 реплики в статусе Running.
- HPA: 2–5 реплик по CPU (порог 50%).
- Мониторинг: Prometheus + Grafana (дашборд 4701), алерт на heap > 80%.

## Ссылки

- Документация Kubernetes: https://kubernetes.io/docs/
- Minikube: https://minikube.sigs.k8s.io/docs/
- Prometheus: https://prometheus.io/docs/
- Grafana: https://grafana.com/docs/