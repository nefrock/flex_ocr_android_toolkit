package com.nefrock.flex_ocr_android_toolkit.processor.recognizer;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;

import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResult;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.api.v1.OnScanListener;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detection;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.Detector;
import com.nefrock.flex_ocr_android_toolkit.processor.detector.DetectorResult;
import com.nefrock.flex_ocr_android_toolkit.util.TFUtil;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelTelRecognizer implements Recognizer {

    private static final String CHARS = "$\\/!@+-%#:().0123456789abcdefghijklmnopqrstuvwxyzぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヴ亜哀挨愛曖悪握圧扱宛嵐安案暗以衣位囲医依委威為畏胃尉異移萎偉椅彙意違維慰遺緯域育一壱逸茨芋引印因咽姻員院淫陰飲隠韻右宇羽雨唄鬱畝浦運雲永泳英映栄営詠影鋭衛易疫益液駅悦越謁閲円延沿炎怨宴媛援園煙猿遠鉛塩演縁艶汚王凹央応往押旺欧殴桜翁奥横岡屋億憶臆乙俺卸音恩温穏下化火加可仮何花佳価果河苛科架夏家荷華菓貨渦過嫁暇禍靴寡歌箇稼課蚊牙瓦我画芽賀雅餓介回灰会快戒改怪拐悔海界皆械絵開階塊楷解潰壊懐諧貝外劾害崖涯街慨蓋該概骸垣柿各角拡革格核殻郭覚較隔閣確獲嚇穫学岳楽額顎掛潟括活喝渇割葛滑褐轄株釜鎌刈干刊甘汗缶完肝官冠巻看陥乾勘患貫寒喚堪換敢棺款間閑勧寛幹感漢慣管関歓監緩憾還館環簡観韓艦鑑丸含岸岩玩眼頑顔願企伎危机気岐希忌汽奇祈季紀軌既記起飢鬼帰基寄規亀喜幾揮期棋貴棄毀旗器畿輝機騎技宜偽欺義疑儀戯擬犠議菊吉喫詰却客脚逆虐九久及弓丘旧休吸朽臼求究泣急級糾宮救球給嗅窮牛去巨居拒拠挙虚許距魚御漁凶共叫狂京享供協況峡挟狭恐恭胸脅強教郷境橋矯鏡競響驚仰暁業凝曲局極玉巾斤均近金菌勤琴筋僅禁緊錦謹襟吟銀区句苦駆具惧愚空偶遇隅串屈掘窟熊繰君訓勲薫軍郡群兄刑形系径茎係型契計恵啓掲渓経蛍敬景軽傾携継詣慶憬稽憩警鶏芸迎鯨隙劇撃激桁欠穴血決結傑潔月犬件見券肩建研県倹兼剣拳軒健険圏堅検嫌献絹遣権憲賢謙鍵繭顕験懸元幻玄言弦限原現舷減源厳己戸古呼固股虎孤弧故枯個庫湖雇誇鼓錮顧五互午呉後娯悟碁語誤護口工公勾孔功巧広甲交光向后好江考行坑孝抗攻更効幸拘肯侯厚恒洪皇紅荒郊香候校耕航貢降高康控梗黄喉慌港硬絞項溝鉱構綱酵稿興衡鋼講購乞号合拷剛傲豪克告谷刻国黒穀酷獄骨駒込頃今困昆恨根婚混痕紺魂墾懇左佐沙査砂唆差詐鎖座挫才再災妻采砕宰栽彩採済祭斎細菜最裁債催塞歳載際埼在材剤財罪崎作削昨柵索策酢搾錯咲冊札刷刹拶殺察撮擦雑皿三山参桟蚕惨産傘散算酸賛残斬暫士子支止氏仕史司四市矢旨死糸至伺志私使刺始姉枝祉肢姿思指施師恣紙脂視紫詞歯嗣試詩資飼誌雌摯賜諮示字寺次耳自似児事侍治持時滋慈辞磁餌璽鹿式識軸七叱失室疾執湿嫉漆質実芝写社車舎者射捨赦斜煮遮謝邪蛇尺借酌釈爵若弱寂手主守朱取狩首殊珠酒腫種趣寿受呪授需儒樹収囚州舟秀周宗拾秋臭修袖終羞習週就衆集愁酬醜蹴襲十汁充住柔重従渋銃獣縦叔祝宿淑粛縮塾熟出述術俊春瞬旬巡盾准殉純循順準潤処初所書庶暑署緒諸女如助序叙徐除小升少召匠床抄肖尚招承昇松沼昭宵将消症祥称笑唱商渉章紹訟勝掌晶焼焦硝粧詔証象傷奨照詳彰障憧衝賞償礁鐘上丈冗条状乗城浄剰常情場畳蒸縄壌嬢錠譲醸色拭食植殖飾触嘱織職辱尻心申伸臣芯身辛侵信津神唇娠振浸真針深紳進森診寝慎新審震薪親人刃仁尽迅甚陣尋腎須図水吹垂炊帥粋衰推酔遂睡穂随髄枢崇数据杉裾寸瀬是井世正生成西声制姓征性青斉政星牲省凄逝清盛婿晴勢聖誠精製誓静請整醒税夕斥石赤昔析席脊隻惜戚責跡積績籍切折拙窃接設雪摂節説舌絶千川仙占先宣専泉浅洗染扇栓旋船戦煎羨腺詮践箋銭潜線遷選薦繊鮮全前善然禅漸膳繕狙阻祖租素措粗組疎訴塑遡礎双壮早争走奏相荘草送倉捜挿桑巣掃曹曽爽窓創喪痩葬装僧想層総遭槽踪操燥霜騒藻造像増憎蔵贈臓即束足促則息捉速側測俗族属賊続卒率存村孫尊損遜他多汰打妥唾堕惰駄太対体耐待怠胎退帯泰堆袋逮替貸隊滞態戴大代台第題滝宅択沢卓拓託濯諾濁達脱奪棚誰丹旦担単炭胆探淡短嘆端綻誕鍛団男段断弾暖談壇地池知値恥致遅痴稚置緻竹畜逐蓄築秩窒茶着嫡中仲虫沖宙忠抽注昼柱衷酎鋳駐著貯丁弔庁兆町長挑帳張彫眺釣頂鳥朝貼超腸跳徴嘲潮澄調聴懲直勅捗沈珍陳賃鎮追椎墜通痛塚漬坪爪鶴低呈廷弟定底抵邸亭貞帝訂庭逓停偵堤提程艇締諦泥的笛摘滴適敵溺迭哲鉄徹撤天典店点展添転填田伝殿電斗吐妬徒途都渡塗賭土奴努度怒刀冬灯当投豆東到逃倒凍唐島桃討透党悼盗陶塔搭棟湯痘登答等筒統稲踏糖頭謄藤闘騰同洞胴動堂童道働銅導瞳峠匿特得督徳篤毒独読栃凸突届屯豚頓貪鈍曇丼那奈内梨謎鍋南軟難二尼弐匂肉虹日入乳尿任妊忍認寧熱年念捻粘燃悩納能脳農濃把波派破覇馬婆罵拝杯背肺俳配排敗廃輩売倍梅培陪媒買賠白伯拍泊迫剥舶博薄麦漠縛爆箱箸畑肌八鉢発髪伐抜罰閥反半氾犯帆汎伴判坂阪板版班畔般販斑飯搬煩頒範繁藩晩番蛮盤比皮妃否批彼披肥非卑飛疲秘被悲扉費碑罷避尾眉美備微鼻膝肘匹必泌筆姫百氷表俵票評漂標苗秒病描猫品浜貧賓頻敏瓶不夫父付布扶府怖阜訃負赴浮婦符富普腐敷膚賦譜侮武部舞封風伏服副幅復福腹複覆払沸仏物粉紛雰噴墳憤奮分文聞丙平兵併並柄陛閉塀幣弊蔽餅米壁璧癖別蔑片辺返変偏遍編弁便勉歩保哺捕補舗母募墓慕暮簿方包芳邦奉宝抱放法泡胞俸倣峰砲崩訪報蜂豊飽褒縫亡乏忙坊妨忘防房肪某冒剖紡望傍帽棒貿貌暴膨謀頬北木朴牧睦僕墨撲没勃堀本奔翻凡盆麻摩磨魔毎妹枚昧埋幕膜枕末抹万満慢漫未味魅岬密蜜脈妙民眠矛務無夢霧娘名命明迷冥盟銘鳴滅免面綿麺茂模毛妄盲耗猛網目黙門紋問冶夜野弥厄役約訳薬躍闇由油喩愉諭輸癒唯友有勇幽悠郵湧猶裕遊雄誘憂融優与予余誉預幼用羊妖洋要容庸揚揺葉陽溶腰様瘍踊窯養擁謡曜抑沃浴欲翌翼拉裸羅来雷頼絡落酪辣乱卵覧濫藍欄吏利里理痢裏履璃離陸立律慄略柳流留竜粒隆硫侶旅虜慮了両良料涼猟陵量僚領寮療瞭糧力緑林厘倫輪隣臨瑠涙累塁類令礼冷励戻例鈴零霊隷齢麗暦歴列劣烈裂恋連廉練錬呂炉賂路露老労弄郎朗浪廊楼漏籠六録麓論和話賄脇惑枠湾腕虞且遵但朕附又幌ー旭「」〜・菱苑伊證R綜D渥E弘&ゞ讃篠朋斯雁柏彦砥曙簾辰函庵榎澗汲茅蛾桔稜汐槻苔幡湊蘭岱銚乃鱒梁樽桂堺狗碓潭磐嶺瑞鷹栖鞆楢鐵萌釧阿々蘇芦俣樺之緋蘂楓栗砺巴丑鳩稔卯嘉糠苫埠澤渕磯萩癸巳葦樫翔鴻渚智淵條笠萱榊橘珸瑤瑁槍罐笏鶉已鷲Y晨稀櫻聚毘峨獅蕨宍粟宕桧涌瓜苅倶蛯霞鮎檜鰔姥鴎爾兜櫓郛淀虻蹄巽盃珊舸轟桝杵ヶ杜碧瑛莫寅楠鞠畠椴砿沓鴛禽樋鯉鳶芭撫蔭瞥爺蟠杖荻昌鵜鳧蓬莱桐笹蔓藁甜蔦萠弗箙螺祢鼈鳳趾宏筑琵琶貰塘鐺裡呑峯柴萢諏佃迦堰茜狼桶鞘漉嶋藏楮舘昴龍狐禰喰蒔櫛窪鮫朔洲廿庄牡烏蕉蓮雛淋蛎艘蛸鷺菰槌屏碇黎蕷溜蒲苺螢李袰陀蟹厩梹榔鐇鎧鰺鯵驫舮舛鰐鯖辻胡菖廻嗽枇杷猪夷賽竿笊蒼岫菩椛雀籏枋蓼曳饅駮獺馳瓢袴杏兎應鍜惣傳鴨埖厨肴繋鉈簗薮鍬駿蟇綾椚臥煤竪鶯摺嬉只芹菅帷叺亦畦鞍噌嚢葭檀箕搦鎗橇鰍苧雫鴬麁卦吠鎚鼠蝦酉狄錢澱會梶榴燕枡椌塒鈎蛤埣竈鹽庚鱈霄箒鮪圃瘻茗亘閖絅隈蕪杢嶽樟杭躰鍔柧忽蒜蛭蟻這楡蓑欅駕侭狢峩甫逢瀞粕竃鴉鰭箟薔薇廼韮秤鳰魁蕗鯲椿儘鰄槐塙轌鞁稗鮒凰筏鼎醍醐櫃鈷鉦醗兀戈梵濡鞦刎葎鑓綴狸抓薗篭叉葹楯椹鮨梓吾廟戊筍暘播槇楪脛晦擶朧梺俎釿鮭其蒄莅叶鴇坦筵榧艮縊燧湘勿禄桙橿蟆柚鐙硯橲甕炮賤壺澳陦熨耻耶梯冨楊戌亥笈胄頤旱轡啼畷簑埓圷籾Z馴猯溪硲佑珂掉鵠跿莚於鉾烟挽樅樛蕎錫蝉鵤國學倭晃荊獨祠挾'曾鴫箭狹祇壬笂疋榛此藪楚醤埜衙嬬橡邑釘QX蕃_L冑縣卉鋏贄糀鵬颪杓衾勒葺—栢垳閏[]靱黛牟佛粂巌、祐犢WⅡⅠ鏑旛萬廣稱泪荏卜磑匝瑳笄埴剃鋸氣醫藝靖麹偕隼聯雙詢澁珈琲,亞箪笥芙蓉燈箔〈〉撰徽壽鋲癌饗鈑榮蝕砧謨濤輿佼『』瀧玲瓏蹊Ⅲ狛舳聘樓槎濾祗菫纒逗秦糟壗柾敦艀麒麟秣捲當蜘翠與鏥鱸竺葵鱗蝶椡莇鰕孟萄杣槙犀頸劔頚扣蚫叡畉彌鮖楜蜷鵯鶚躬晒騨亟茄斐鸞燐閤峅搗娚柑董閨祓飴×繩窕禿剱緤咋礫伽灘逹爼茱撚甑椙栂鬚糺嵩賑垈棡嵯榑舂鉋鑪篶伍皐喬芥戎壜禾揖黍猷湫椋甥袈裟櫨薩薙漕蜆涛鼡鵺訶玖齊鰯孕萸墹邨杁蝮橦鋺鯱筬淺莨敲輌掖蟷螂惟凪駈鯛眞絋圦縞鶇帖崗效瘤凧柊泙麩莪碕梛斧賣鰹桴鯏鯰籔釆粥蠣肱磧皷柘枅琉鮠慥婁邇禮焔綣櫟堵柞嶬藺綺杠琢蛙菴榿靭閻坤皀莢蘆庇曼沮麸樵也猩笋滕駝椥轆轤枳錺篝罧淳樒芒餉蜻蛉撞泓耆楳髭洛筈菟岼莵欽珀伶經鹸讀茸秬蛋輛蔀凌鍼灸寳杤夙籟湛乎炬椒劫鷆孑桾甸筱糯毫鈩檪掎觜哉耀柤鵲祁礒苣薑椣枌汀鰈樮鬮杙歎桷庖忰鋤釛箆淞杼遙坏簸圭閂煉弩槁臘呰齋乢垪岨幟猴渠縢糘垢棯峇粭垰燿畭栩饌剪實孵詫苞饒蒋禎侈鯆碆薊〔〕埆渭晋檮梼鞭蜑勺琳筥辨處濠曰姪駛潴樂頴糒祷濱惠餘諫紐朶酘窄硴莎栴泗枦栫苓碩稙葱礬掻筌裳衲飫浩聾姶奄娃餠假㈱尖岻矼玻";
    private static final int NUM_BATCHES = 1;
    private static final int MAX_STRING_LEN = 50;

    private Interpreter interpreter;
    private final Context context;
    private final String modelPath;
    private final int inputX;
    private final int inputY;

    private ByteBuffer imgData;
    private float[][][] outputPutValues;
    private final CTCTextDecoder decoder;

    public LabelTelRecognizer(Context context, String modelPath, Size inputSize) {
        this.context = context;
        this.modelPath = modelPath;
        this.inputX = inputSize.getWidth();
        this.inputY = inputSize.getHeight();
        this.decoder = new CTCTextDecoder(CHARS);
    }

    @Override
    public void process(Mat mat, Detector detector, FlexScanOption option, OnScanListener<FlexScanResults> listener) {
        DetectorResult detectorResult = detector.process(mat, option);
        List<Detection> detections = detectorResult.getDetections();

        long t1 = SystemClock.uptimeMillis();
        List<FlexScanResult> results = new ArrayList<>();
        for (Detection detection : detections) {
            if(detection.getClassID() == 0) {
                FlexScanResultType typ = FlexScanResultType.INVOICE_LABEL;
                results.add(new FlexScanResult(typ, null, detection.getConfidence(), detection.getBoundingBox()));
                continue;
            }
            org.opencv.core.Rect cvBBox = detection.getCvBoundingBox();
            Mat cropped = new Mat(mat, cvBBox);
            Mat imgResized = new Mat();
            //FIXME: 単純にリサイズしないでアスペクト比を保つこと
            Imgproc.resize(cropped, imgResized, new org.opencv.core.Size(inputX, inputY));
            Mat imgRotated = new Mat();
            Core.rotate(imgResized, imgRotated, Core.ROTATE_90_CLOCKWISE);
            Imgproc.cvtColor(imgRotated, imgRotated, Imgproc.COLOR_RGB2GRAY);
            imgData.rewind();
            for (int i = 0; i < inputX; ++i) {
                for (int j = 0; j < inputY; ++j) {
                    int pixelValue = (int) imgRotated.get(i, j)[0];
                    float v = pixelValue / 255.0f;
                    imgData.putFloat(v);
                }
            }
            Map<Integer, Object> outputMap = new HashMap<>();
            outputMap.put(0, outputPutValues);
            Object[] inputArray = {imgData};
            // Run the inference call.
            interpreter.runForMultipleInputsOutputs(inputArray, outputMap);
            String text = decoder.decode(outputPutValues[0]);
            Log.d("process", text);
            FlexScanResultType typ = FlexScanResultType.TELEPHONE_NUMBER;
            results.add(new FlexScanResult(typ, text, 1.0, detection.getBoundingBox()));
        }
        long t2 = SystemClock.uptimeMillis();
        long elapsed = t2 - t1;
        listener.onScan(new FlexScanResults(results, elapsed));
    }

    @Override
    public void init() {
        try {
            MappedByteBuffer modelFile = TFUtil.loadModelFile(context.getAssets(), this.modelPath);
            Interpreter.Options options = new Interpreter.Options();

//            GpuDelegate delegate = new GpuDelegate();
//            options.addDelegate(delegate);
            options.setUseXNNPACK(true);
            options.setNumThreads(3);
            interpreter = new Interpreter(modelFile, options);
            int numBytesPerChannel = 4; //floating point
            imgData = ByteBuffer.allocateDirect(NUM_BATCHES * inputX * inputY * 1 * numBytesPerChannel);
            imgData.order(ByteOrder.nativeOrder());
            outputPutValues = new float[NUM_BATCHES][MAX_STRING_LEN][CHARS.length() + 3];

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
    }
}
