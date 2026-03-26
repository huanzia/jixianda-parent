const fs=require('fs');
const mapPath=process.argv[2];
const txt=fs.readFileSync(mapPath,'utf8').replace(/^\uFEFF/,'');
const map=JSON.parse(txt);
const targets=['pages/api/api.js','pages/index/index.js','pages/order/index.js'];
for(const t of targets){
  const i=map.sources.findIndex(s=>s.endsWith(t));
  if(i===-1){
    console.log('MISS '+t);
  }else{
    const src=map.sourcesContent&&map.sourcesContent[i]?map.sourcesContent[i]:'';
    console.log('HIT '+t+' => '+map.sources[i]+' len='+src.length);
  }
}
