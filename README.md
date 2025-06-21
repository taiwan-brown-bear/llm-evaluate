step 1: clone the repos to IDE

step 2: start ollama llm locally

step 3: set env. variables for llm api keys for openai llm and anthropic llm

step 4: use curl to ask the question (e.g., 

    curl --location 'http://localhost:8080/llm/evaluate' \
    --header 'Content-Type: application/json' \
    --data '{
    "request": "where is Taiwan ?",
    "systemMessage": "you are a expert"
    }'

)

step 5: the response will have the reqeust id (e.g.,

    {
    "requestId": 3052,
    "request": "where is Taiwan ?",
    "systemMessage": "you are a expert",
    "targetModelEvaluationResults": [
        {
            "targetModel": "claude-3-7-sonnet-20250219",
            "targetModelResponse": "Taiwan is an island...

)

step 6: use the requestId to get the report by running the sql statement (e.g., 

    mysql> select a.target_model, 
                  a.evaluated_by, 
                  a.confidence, 
                  b.request, 
                  b.system_message 
    from llm_evaluate_results a, llm_evaluate_requests b
    where a.request_id = b.request_id 
          and 
          b.request_id = 3052;

    +----------------------------+----------------------------+------------+-------------------+------------------+
    | target_model               | evaluated_by               | confidence | request           | system_message   |
    +----------------------------+----------------------------+------------+-------------------+------------------+
    | claude-3-7-sonnet-20250219 | claude-3-7-sonnet-20250219 |       0.98 | where is Taiwan ? | you are a expert |
    | claude-3-7-sonnet-20250219 | tinyllama:latest           |       NULL | where is Taiwan ? | you are a expert |
    | claude-3-7-sonnet-20250219 | gpt-4o-mini-2024-07-18     |          1 | where is Taiwan ? | you are a expert |
    | tinyllama:latest           | claude-3-7-sonnet-20250219 |       0.95 | where is Taiwan ? | you are a expert |
    | tinyllama:latest           | tinyllama:latest           |       NULL | where is Taiwan ? | you are a expert |
    | tinyllama:latest           | gpt-4o-mini-2024-07-18     |        0.7 | where is Taiwan ? | you are a expert |
    | gpt-4o-mini-2024-07-18     | claude-3-7-sonnet-20250219 |       0.98 | where is Taiwan ? | you are a expert |
    | gpt-4o-mini-2024-07-18     | tinyllama:latest           |       NULL | where is Taiwan ? | you are a expert |
    | gpt-4o-mini-2024-07-18     | gpt-4o-mini-2024-07-18     |          1 | where is Taiwan ? | you are a expert |
    +----------------------------+----------------------------+------------+-------------------+------------------+
    9 rows in set (0.036 sec)

)
