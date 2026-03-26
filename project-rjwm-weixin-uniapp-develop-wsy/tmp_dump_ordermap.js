const fs=require('fs');
const map=JSON.parse(fs.readFileSync(process.argv[2],'utf8').replace(/^\uFEFF/,''));
map.sourcesContent.forEach((c,i)=>{
  console.log('\n---',i,map.sources[i],'len',c?c.length:0);
  console.log((c||'').slice(0,300).replace(/\n/g,'\\n'));
});
