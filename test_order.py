#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
from decimal import Decimal
from typing import Any, Dict, List, Optional

import requests


# =========================
# 配置区
# =========================
SERVER_URL = "http://localhost:58088"
USER_URL = "http://localhost:58081"
TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NzI1NTkyNDMsInVzZXJJZCI6MX0.qhELoaG_9w2u5atE3OAo2DT8U7IEVeP8SV4RDCrLs1w"  # 留空请手动填入用户 authentication token
DISH_NAME = "王老吉"
WAREHOUSE_ID = 1
TIMEOUT = 10


class C:
    INFO = "\033[96m"
    OK = "\033[92m"
    FAIL = "\033[91m"
    WARN = "\033[93m"
    END = "\033[0m"


def log_info(msg: str) -> None:
    print(f"{C.INFO}[INFO]{C.END} {msg}")


def log_ok(msg: str) -> None:
    print(f"{C.OK}[SUCCESS]{C.END} {msg}")


def log_fail(msg: str) -> None:
    print(f"{C.FAIL}[FAIL]{C.END} {msg}")


def fatal(msg: str) -> None:
    log_fail(msg)
    sys.exit(1)


def auth_headers() -> Dict[str, str]:
    h = {"Content-Type": "application/json"}
    if TOKEN.strip():
        h["authentication"] = TOKEN.strip()
    return h


def request_json(method: str, url: str, step: str, **kwargs) -> Dict[str, Any]:
    try:
        resp = requests.request(method=method, url=url, timeout=TIMEOUT, **kwargs)
    except requests.RequestException as e:
        fatal(f"{step} 请求异常: {e}")
    if resp.status_code != 200:
        fatal(f"{step} HTTP!=200, status={resp.status_code}, body={resp.text}")
    try:
        return resp.json()
    except Exception:
        fatal(f"{step} 响应非JSON, body={resp.text}")
    return {}


def assert_code_1(payload: Dict[str, Any], step: str) -> None:
    if payload.get("code") != 1:
        fatal(f"{step} 业务失败: code={payload.get('code')}, msg={payload.get('msg')}, data={payload.get('data')}")


def ensure_token() -> None:
    if not TOKEN.strip():
        fatal("TOKEN 为空。请先在脚本顶部填入用户 authentication token。")


def step1_prepare_address() -> int:
    log_info("Step 1 (User): 获取地址簿")
    list_url = f"{USER_URL}/user/addressBook/list"
    data = request_json("GET", list_url, "地址簿查询", headers=auth_headers())
    assert_code_1(data, "地址簿查询")
    addresses = data.get("data") or []
    if addresses:
        addr_id = addresses[0].get("id")
        if addr_id is None:
            fatal("地址簿首条记录缺少 id")
        log_ok(f"地址簿可用，addressId={addr_id}")
        return int(addr_id)

    log_info("地址簿为空，创建测试地址")
    create_url = f"{USER_URL}/user/addressBook"
    create_body = {
        "consignee": "E2E Tester",
        "phone": "13800138000",
        "sex": "1",
        "detail": "北京市朝阳区测试路100号",
        "label": "公司",
        "isDefault": 1
    }
    created = request_json("POST", create_url, "地址创建", headers=auth_headers(), json=create_body)
    assert_code_1(created, "地址创建")

    data2 = request_json("GET", list_url, "地址簿重查", headers=auth_headers())
    assert_code_1(data2, "地址簿重查")
    addresses2 = data2.get("data") or []
    if not addresses2:
        fatal("创建地址后仍为空")
    addr_id = addresses2[0].get("id")
    if addr_id is None:
        fatal("创建后地址缺少 id")

    # 设为默认地址（防止下单读取默认地址失败）
    set_default_url = f"{USER_URL}/user/addressBook/default"
    set_default_body = {"id": int(addr_id)}
    default_ret = request_json("PUT", set_default_url, "设置默认地址", headers=auth_headers(), json=set_default_body)
    assert_code_1(default_ret, "设置默认地址")

    log_ok(f"创建并设置默认地址成功，addressId={addr_id}")
    return int(addr_id)


def step2_search_dish() -> (int, Decimal):
    log_info("Step 2 (Server): 搜索商品")
    search_url = f"{SERVER_URL}/user/sku/search"
    params = {"name": DISH_NAME, "warehouseId": WAREHOUSE_ID}
    data = request_json("GET", search_url, "商品搜索", headers=auth_headers(), params=params)
    assert_code_1(data, "商品搜索")
    items: List[Dict[str, Any]] = data.get("data") or []
    if not items:
        fatal(f"搜索无结果: name={DISH_NAME}, warehouseId={WAREHOUSE_ID}")

    target: Optional[Dict[str, Any]] = None
    for i in items:
        if str(i.get("name", "")).strip() == DISH_NAME:
            target = i
            break
    if target is None:
        target = items[0]
        log_info(f"未找到精确同名，使用首条: {target.get('name')}")

    dish_id = target.get("id")
    price_raw = target.get("price")
    if dish_id is None or price_raw is None:
        fatal(f"搜索结果缺少 id/price: {target}")
    price = Decimal(str(price_raw))
    log_ok(f"命中商品: dishId={dish_id}, price={price}")
    return int(dish_id), price


def step3_cart_ops(dish_id: int) -> None:
    log_info("Step 3 (Server): 清空购物车 -> 添加购物车")
    clean_url = f"{SERVER_URL}/user/shoppingCart/clean"
    clean = request_json("DELETE", clean_url, "购物车清空", headers=auth_headers())
    assert_code_1(clean, "购物车清空")

    add_url = f"{SERVER_URL}/user/shoppingCart/add"
    add_body = {
        "dishId": dish_id,
        "warehouseId": WAREHOUSE_ID,
        "dishFlavor": ""
    }
    added = request_json("POST", add_url, "购物车添加", headers=auth_headers(), json=add_body)
    assert_code_1(added, "购物车添加")
    log_ok("购物车操作完成")


def step4_submit_order(address_id: int, amount: Decimal) -> str:
    log_info("Step 4 (Server): 提交订单")
    submit_url = f"{SERVER_URL}/user/order/submit"
    submit_body = {
        "warehouseId": WAREHOUSE_ID,
        "addressBookId": address_id,
        "payMethod": 1,
        "remark": "E2E test order",
        "deliveryStatus": 1,
        "tablewareNumber": 0,
        "tablewareStatus": 0,
        "packAmount": 0,
        "amount": float(amount)
    }
    submitted = request_json("POST", submit_url, "订单提交", headers=auth_headers(), json=submit_body)
    assert_code_1(submitted, "订单提交")
    order_number = ((submitted.get("data") or {}).get("orderNumber") or "").strip()
    if not order_number:
        fatal(f"订单提交成功但缺少 orderNumber: {submitted}")
    log_ok(f"订单提交成功, orderNumber={order_number}")
    return order_number


def step5_pay(order_number: str) -> None:
    log_info("Step 5 (Server): 模拟支付")
    pay_url = f"{SERVER_URL}/user/order/payment"
    pay_body = {"orderNumber": order_number, "payMethod": 1}
    paid = request_json("PUT", pay_url, "订单支付", headers=auth_headers(), json=pay_body)
    assert_code_1(paid, "订单支付")
    log_ok("支付接口调用成功")


def step6_verify(order_number: str) -> None:
    log_info("Step 6 (Server): 验证订单状态")
    history_url = f"{SERVER_URL}/user/order/historyOrders"
    history = request_json("GET", history_url, "历史订单查询", headers=auth_headers(), params={"page": 1, "pageSize": 10})
    assert_code_1(history, "历史订单查询")
    records = ((history.get("data") or {}).get("records") or [])
    if not records:
        fatal("历史订单为空，无法校验状态")

    matched = None
    for r in records:
        if str(r.get("number", "")).strip() == order_number:
            matched = r
            break
    if matched is None:
        fatal(f"历史订单中未找到 orderNumber={order_number}")

    status = matched.get("status")
    if status is None:
        fatal(f"目标订单缺少 status: {matched}")
    if int(status) < 3:
        fatal(f"状态断言失败: status={status}, 期望>=3")
    log_ok(f"状态校验通过: orderNumber={order_number}, status={status}")


def main():
    ensure_token()
    log_info(f"SERVER_URL={SERVER_URL}")
    log_info(f"USER_URL={USER_URL}")
    log_info(f"DISH_NAME={DISH_NAME}")

    address_id = step1_prepare_address()
    dish_id, price = step2_search_dish()
    step3_cart_ops(dish_id)
    order_number = step4_submit_order(address_id, price)
    step5_pay(order_number)
    step6_verify(order_number)
    log_ok("全链路测试通过")


if __name__ == "__main__":
    main()
