const fs=require('fs');
const map=JSON.parse(fs.readFileSync(process.argv[2],'utf8').replace(/^\uFEFF/,''));
console.log('sources len',map.sources.length);
map.sources.forEach((s,i)=>{ if(i<20) console.log(i,s);});
for(let i=0;i<map.sources.length;i++) if(map.sources[i].includes('pages/order/index.js')) console.log('FOUND',i,map.sources[i], (map.sourcesContent[i]||'').length);
