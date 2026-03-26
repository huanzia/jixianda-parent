#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import sys
import pymysql


DB_HOST = os.getenv("DB_HOST", "127.0.0.1")
DB_PORT = int(os.getenv("DB_PORT", "3307"))
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "root")
DB_NAME = os.getenv("DB_NAME", "jixianda")


def main():
    print("[INFO] connecting mysql...")
    try:
        conn = pymysql.connect(
            host=DB_HOST,
            port=DB_PORT,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,
            charset="utf8mb4",
            cursorclass=pymysql.cursors.DictCursor,
            connect_timeout=8,
            read_timeout=8,
            write_timeout=8,
        )
    except Exception as e:
        print(f"[FAIL] db connect failed: {e}")
        return 1

    sql = """
    SELECT id, number, status, order_time
    FROM orders
    ORDER BY id DESC
    LIMIT 5
    """
    try:
        with conn.cursor() as cur:
            cur.execute(sql)
            rows = cur.fetchall()
    except Exception as e:
        print(f"[FAIL] query failed: {e}")
        conn.close()
        return 1
    finally:
        conn.close()

    if not rows:
        print("[WARN] no rows in orders")
        return 0

    print("[SUCCESS] latest 5 orders:")
    print("-" * 78)
    print(f"{'id':<10}{'number':<24}{'status':<10}{'order_time'}")
    print("-" * 78)
    for r in rows:
        print(f"{str(r.get('id')):<10}{str(r.get('number')):<24}{str(r.get('status')):<10}{str(r.get('order_time'))}")
    print("-" * 78)
    return 0


if __name__ == "__main__":
    sys.exit(main())

