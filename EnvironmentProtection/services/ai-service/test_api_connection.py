"""
API连接测试脚本
"""
import sys
import os

# 添加项目路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from app.core.config import settings
import httpx


def test_llm_api():
    """测试LLM API连接"""
    print("=" * 60)
    print("Testing LLM API Connection")
    print("=" * 60)
    print(f"Base URL: {settings.LLM_BASE_URL}")
    print(f"Model: {settings.LLM_MODEL}")
    print(f"API Key: {settings.LLM_API_KEY[:20]}..." if len(settings.LLM_API_KEY) > 20 else "Not set")
    print()

    # 测试API连接 - 使用简单的Bearer Token方式
    url = f"{settings.LLM_BASE_URL}/chat/completions"
    print(f"Request URL: {url}")
    print()

    payload = {
        "model": settings.LLM_MODEL,
        "messages": [
            {"role": "user", "content": "Say 'Connection successful' in Chinese"}
        ],
        "temperature": 0.7,
        "max_tokens": 100
    }

    try:
        print("Sending test request...")

        # 方式1: 简单Bearer Token
        print("\n[Test 1] Using simple Bearer token...")
        with httpx.Client(timeout=60.0) as client:
            response = client.post(
                url,
                json=payload,
                headers={
                    "Content-Type": "application/json",
                    "Authorization": f"Bearer {settings.LLM_API_KEY}"
                }
            )

            print(f"Status code: {response.status_code}")

            if response.status_code == 200:
                result = response.json()
                print("[OK] API connected successfully!")
                print()
                print("Response content:")
                print("-" * 40)
                content = result.get("choices", [{}])[0].get("message", {}).get("content", "")
                print(content)
                print("-" * 40)
                return True
            else:
                print(f"[FAIL] Error: {response.text}")

        # 方式2: 使用doubao_client的chat方法
        print("\n[Test 2] Using doubao_client.chat()...")
        from app.core.doubao_client import doubao_client

        result = doubao_client.chat(
            messages=[{"role": "user", "content": "Say 'Connection successful' in Chinese"}]
        )

        if "API请求失败" not in result and "请求异常" not in result:
            print("[OK] doubao_client works!")
            print()
            print("Response content:")
            print("-" * 40)
            print(result)
            print("-" * 40)
            return True
        else:
            print(f"[FAIL] doubao_client error: {result}")
            return False

    except httpx.ConnectError as e:
        print(f"[FAIL] Connection error: Cannot connect to server")
        print(f"Details: {e}")
        return False
    except httpx.TimeoutException as e:
        print(f"[FAIL] Request timeout")
        print(f"Details: {e}")
        return False
    except Exception as e:
        print(f"[FAIL] Request exception: {e}")
        import traceback
        traceback.print_exc()
        return False


def test_mysql_connection():
    """测试MySQL连接"""
    print()
    print("=" * 60)
    print("测试 MySQL 数据库连接")
    print("=" * 60)
    print(f"Host: {settings.MYSQL_HOST}:{settings.MYSQL_PORT}")
    print(f"Database: {settings.MYSQL_DATABASE}")
    print(f"User: {settings.MYSQL_USER}")
    print()

    try:
        import pymysql
        conn = pymysql.connect(
            host=settings.MYSQL_HOST,
            port=settings.MYSQL_PORT,
            user=settings.MYSQL_USER,
            password=settings.MYSQL_PASSWORD,
            database=settings.MYSQL_DATABASE,
            charset='utf8mb4',
            connect_timeout=10
        )

        print("[OK] MySQL connected successfully!")

        # 检查表是否存在
        cursor = conn.cursor()
        cursor.execute("SHOW TABLES")
        tables = cursor.fetchall()

        print()
        print("数据库中的表:")
        for table in tables:
            print(f"  - {table[0]}")

        cursor.close()
        conn.close()
        return True

    except pymysql.ConnectError as e:
        print(f"[FAIL] MySQL connection failed")
        print(f"Details: {e}")
        return False
    except Exception as e:
        print(f"[FAIL] Exception: {e}")
        return False


if __name__ == "__main__":
    print()
    print("API Connection Test Tool")
    print("=" * 60)
    print()

    llm_success = test_llm_api()
    mysql_success = test_mysql_connection()

    print()
    print("Test Summary")
    print("=" * 60)
    print(f"LLM API:  {'[OK] Success' if llm_success else '[FAIL] Failed'}")
    print(f"MySQL:    {'[OK] Success' if mysql_success else '[FAIL] Failed'}")
    print()

    if llm_success and mysql_success:
        print("All services connected successfully!")
    else:
        print("Some services failed to connect, please check the configuration")
