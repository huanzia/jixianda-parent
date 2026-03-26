const fs=require('fs');
const map=JSON.parse(fs.readFileSync(process.argv[2],'utf8').replace(/^\uFEFF/,''));
for(const s of map.sources){
  if(s.includes('pages/order')||s.includes('pages/index')||s.includes('pages/api')) console.log(s);
}
