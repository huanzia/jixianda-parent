#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
极鲜达 - 全链路搜索测试脚本 (本地调试版)
"""

import sys
import requests
import json

# ================= 配置区域 (已根据你的环境修改) =================

# 1. 后端服务地址 (你的服务运行在 58088)
BASE_URL = "http://localhost:58088"

# 2. 测试目标菜品
TEST_DISH_NAME = "王老吉"

# 3. [关键] 鉴权 Token
# 请将你在 Java 中运行 TokenGenTest 生成的字符串粘贴在引号内
TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NzI1NTY5ODcsInVzZXJJZCI6MX0.92tVs6b9GKkNQSyAyxA3WAImpQMWQZgSaWnehKI1dVY"

# ==============================================================

# 颜色定义
class C:
    GREEN = "\033[92m"
    RED = "\033[91m"
    CYAN = "\033[96m"
    RESET = "\033[0m"

def ok(msg: str):
    print(f"{C.GREEN}[SUCCESS]{C.RESET} {msg}")

def fail(msg: str):
    print(f"{C.RED}[FAIL]{C.RESET} {msg}")

def info(msg: str):
    print(f"{C.CYAN}[INFO]{C.RESET} {msg}")

def build_headers():
    h = {
        "Content-Type": "application/json",
        "authentication": TOKEN  # 极鲜达项目约定的 Header Key
    }
    return h

def call_search(name: str, warehouse_id: int):
    url = f"{BASE_URL}/user/sku/search"
    params = {"name": name, "warehouseId": warehouse_id}

    info(f"请求: GET {url} | 参数: {params}")

    try:
        r = requests.get(url, params=params, headers=build_headers(), timeout=5)
        # 尝试解析 JSON
        try:
            body = r.json()
        except:
            body = {}
        return r.status_code, body, r.text
    except Exception as e:
        return -1, {}, str(e)

def contains_dish(resp_json: dict, dish_name: str) -> bool:
    # 解析 Result<List<DishVO>> 结构
    data_list = resp_json.get("data")
    if not data_list:
        return False

    for item in data_list:
        # 模糊匹配：只要商品名包含 "王老吉" 就算命中
        if dish_name in str(item.get("name", "")):
            print(f"   -> 命中商品: {item.get('name')} | 价格: {item.get('price')} | 销量: {item.get('sales')}")
            return True
    return False

def main():
    print("="*50)
    info(f"目标环境: {BASE_URL}")
    info(f"测试菜品: {TEST_DISH_NAME}")

    if "粘贴" in TOKEN or len(TOKEN) < 10:
        fail("请先在脚本第 20 行填入有效的 Token！")
        return

    # ---------------------------------------------------------
    # 场景A: 北京仓(ID=1) -> 应该有货
    # ---------------------------------------------------------
    print("-" * 30)
    status, resp, raw = call_search(TEST_DISH_NAME, 1)

    if status != 200:
        fail(f"HTTP请求失败: {status}")
        print(raw)
        return

    if resp.get("code") != 1:
        fail(f"业务返回失败: {resp.get('msg')}")
        return

    if contains_dish(resp, TEST_DISH_NAME):
        ok("场景A通过：北京仓(ID=1) 成功搜索到商品")
    else:
        fail(f"场景A失败：北京仓未找到 '{TEST_DISH_NAME}'")
        print(f"调试信息: {json.dumps(resp, ensure_ascii=False, indent=2)}")
        return

    # ---------------------------------------------------------
    # 场景B: 上海仓(ID=2) -> 应该无货
    # ---------------------------------------------------------
    print("-" * 30)
    status, resp, raw = call_search(TEST_DISH_NAME, 2)

    if contains_dish(resp, TEST_DISH_NAME):
        fail(f"场景B失败：上海仓(ID=2) 居然搜到了商品 (预期应为空)")
        return
    else:
        ok("场景B通过：上海仓(ID=2) 未搜到商品 (符合预期)")

    print("="*50)
    ok("🎉 全链路测试通过！ES 分仓搜索逻辑正常！")

if __name__ == "__main__":
    main()