const fs=require('fs');
const map=JSON.parse(fs.readFileSync(process.argv[2],'utf8').replace(/^\uFEFF/,''));
['pages/api/api.js','pages/index/index.js','pages/order/index.js','index.js?vue&type=script&lang=js&'].forEach(k=>{
  let found=0;
  map.sources.forEach((s,i)=>{ if(s.includes(k)){ found++; console.log(i,s,'len', (map.sourcesContent[i]||'').length); } });
  console.log('--- key',k,'found',found);
});
