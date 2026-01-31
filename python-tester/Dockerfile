FROM python:3.11-slim

ENV PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1

WORKDIR /app

RUN groupadd -r dialout && useradd -r -g dialout appuser

COPY requirements.txt ./
RUN pip install --upgrade pip \
    && pip install -r requirements.txt

COPY src ./src
COPY tests ./tests

RUN mkdir -p /app/test-results \
    && chown -R appuser:dialout /app

USER appuser

CMD ["python", "-m", "src.cli"]
