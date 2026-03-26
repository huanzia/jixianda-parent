-- 这个脚本用 Redis 原子地完成库存预扣，目的是避免高并发下出现超卖。
-- KEYS[1] 是库存 key，表示某个仓某个商品的可用秒杀库存。
-- 脚本成功时返回 1，表示库存已经被成功扣减；失败时返回 0，表示库存不足或 key 不存在。
local stock = tonumber(redis.call('GET', KEYS[1]))
if (not stock) or (stock <= 0) then
    return 0
end

-- 只有当当前库存大于 0 时才会执行扣减，保证检查和更新在同一个 Redis 原子操作里完成。
redis.call('DECR', KEYS[1])
return 1
