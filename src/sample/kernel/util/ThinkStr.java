package sample.kernel.util;

public enum ThinkStr {
	
	welcome("مرحبا", "Welcome", "Bienvenue"),
	connection_failed("الاتّصال مقطوع أو بيانات مرفوضة", "Connection failed or refused data", "Connexion échouée ou données réfusées"),
	upload_failed("فشل التّنزيل", "Upload failed", "Chargement échoué"),
	download_failed("فشل التّحميل", "Download failed", "Téléchargement échoué"),
	uploads_manager("مدير التّنزيلات", "Uploads Manager", "La gestion des chargements"),
	downloads_manager("مدير التّحميلات", "Downloads Manager", "La gestion des téléchargements"),
	play("تشغيل", "Play", "Lancer"),
	stop("إيقاف", "Stop", "Arrêter"),
	delete("إزالة", "Delete", "Supprimer"),
	ideaRelated("الفكرة", "The Idea", "L'idée"),
	source("المصدر", "Source", "Source"),
	destination("الوجهة" ,"Destination", "Destination"),
	idea_create("إنشاء فكرة", "Create an idea", "Créer une idée"),
	idea_undone("فكرة غير مطبّقة", "Undone Idea", "Idée non faite"),
	description("الوصف", "Description", "Description"),
	urls("الرّوابط", "URLs", "URLs"),
	save("حفظ", "Save", "Sauvegarder"),
	hour("السّاعة", "Hour", "Heure"),
	minute("الدّقيقة", "Minute", "Minute"),
	upload("تنزيل", "Upload", "Charger"),
	download("تحميل", "Download", "Télécharger"),
	download_upload("تحميل وتنزيل", "Download & Upload", "Télécharger & Charger"),
	workNote_create("إنشاء ملاحظة عمل", "Create a WorkNote", "Créer une WorkNote"),
	done("تمّ", "Done", "Faite"),
	undone("غير تامّ", "Undone", "Non faite"),
	done_undone("غيرتامّ/تمّ", "Done/Undone", "Faite/Non Faite"),
	task("مهمّة", "Task", "Tâche"),
	continuee("متابعة", "Continue", "Continuer"),
	task_date_false("تاريخ المهمّة خاطئ", "Task dates are false", "Les dates de la tâche sont érronées"),
	date_false("تاريخ خاطئ", "False Date", "Date érronée"),
	date_worknote_task_false("تاريخ ملاحظة العمل خاطئ للمهمّة", "The WorkNote date is false according to the Task", "La WorkNote est érronée selon la tâche liée"),
	task_configure("إعداد مهمّة", "Task Configuration", "Configurer la tâche"),
	startDate("موعد البداية", "Start date", "Date début"),
	finishDate("موعد النّهاية", "Finish date", "Date fin"),
	submit("تأكيد", "Submit", "Appliquer"),
	back("رجوع", "Back", "Retour"),
	load("تحميل", "Loading", "Chargement"),
	load_manager("مركز التّحميلات", "Load Manager", "Gestion du chargement"),
	all_files("كل الملفّات", "All Files", "Tous les fichiers"),
	browser_ideas("متصفّح الأفكار", "Ideas Browser", "Le navigateur des idées"),
	browser_dir("متصفّح المجلّدات", "Folders Browser", "Le navigateur des dossiers"),
	browser_attachments("متصفّح المرفقات", "Attachments Browser", "Le navigateur des attachements"),
	double_click_remove("نقرة مزدوجة لإزالة الفكرة المحدّدة", "Double click to remove the selected idea", "Double clique pour supprimer l'idée séletionnée"),
	enter_id_ok("أدخل تعريف الفكرة ثم اضغط على إدخال", "Enter an idea's id then click ENTER", "Entrer l'id de l'idée puis cliquer ENTRER"),
	enter_id_ok_remove("أدخل تعريف الفكرة ثم اضغط على إدخال، ونقرة مزدوجة لإزالة الفكرة المحدّدة", "Enter an idea's id then click ENTER and double click to remove the selected idea", "Entrer l'id de l'idée puis cliquer ENTRER et double clique pour supprimer l'idée séletionnée"),
	general("عامّ", "General", "Général"),
	id("تعريف", "ID", "ID"),
	title("العنوان", "Title", "Titre"),
	date_and_time("التّاريخ والوقت", "Date and clock", "Horloge et date"),
	related_ideas("الأفكار المتعلّقة", "Related Ideas", "Idées liées"),
	workNotes("ملاحظات العمل", "WorkNotes", "WorkNotes"),
	workNote("ملاحظة عمل", "WorkNote", "WorkNote"),
	attachments("المرفقات", "Attachments", "Attachements"),
	toUploads("للتّنزيل", "To upload", "Pour Charger"),
	double_click_edit("نقرة مزدوجة للتّحرير", "Double click to edit", "Double clique pour éditer"),
	editor("محرّر", "Editor", "Editeur"),
	edit("تحرير", "Edit", "Edition"),
	to_find("للبحث", "To find", "Pour trouver"),
	to_replace_with("للاستبدال به", "To replace with", "Remplacer avec"),
	case_sensitive("تحسّس الحروف", "Case sensitive", "Respecter la casse"),
	next("التّالي", "Next", "Suivant"),
	replace("استبدال", "Replace", "Remplacer"),
	replace_all("استبدال الكلّ", "Replace all", "Remplacer tous"),
	replace_find("بحث/استبدال", "Find/Replace", "Trouver/Remplacer"),
	orientation("تغيير الاتّجاه", "Change Orientation", "Changer l'orientation"),
	not_found("غير موجود", "Not found", "Non trouvé"),
	local_path("مسار محليّ", "Local path", "Lien local"),
	extern_path("مسار خارجيّ", "Extern path", "Lien externe"),
	add("إضافة", "Add", "Ajouter"),
	notify("إشعار", "Notify", "Notifier"),
	period("الدّور", "Period", "Période"),
	once("مرّة واحدة", "Once", "Une seule fois"),
	daily("يوميّا", "Daily", "Par jour"),
	weekly("أسبوعيّا", "Weekly", "Par semaine"),
	monthly("شهريّا", "Monthly", "Par mois"),
	yearly("سنويّا", "Yearly", "Par année"),
	search_idea("ابحث عن فكرة", "Search an Idea", "Chercher une Idée"),
	choose_date("اختر تاريخا", "Choose a date", "Choisir une date"),
	year("السّنة", "Year", "Année"),
	reset("إعادة", "Reset", "Remise"),
	filter("فرز", "Filter", "Filtrer"),
	idea("فكرة", "Idea", "Idée"),
	ideas_table("جدول الأفكار", "Ideas Table", "Table des idées"),
	calendar("الرّزنامة", "Calendar", "Calendrier"),
	settings("الإعدادات", "Settings", "Paramètres"),
	infos("معلومات", "Information", "Informations"),
	ideas_undone_notifs("إشعارات الأفكار غير المنهيّة", "Undone ideas notifications", "Notifications des idées pour faire"),
	task_notifs("إشعارات المهمّات", "Tasks notifications", "Notifications des tâches"),
	new_idea("فكرة جديدة", "New Idea", "Nouvelle idée"),
	new_worknote("ملاحظة عمل جديدة", "New WorkNote", "Nouvelle WorkNote"),
	import_export("تصدير واستيراد", "Import & Export", "Importer & Exporter"),
	ideas_import("استيراد الأفكار", "Import Ideas", "Importer des idées"),
	table_export("تصدير الأفكار من الجدول الحاليّ", "Export from current Ideas Table", "Exporter du table courant des idées"),
	downloads("تحميلات", "Downloads", "Téléchargements"),
	uploads("تنزيلات", "Uploads", "Chargements"),
	refresh("تحديث", "Refresh", "Rafraîchir"),
	import_("استيراد", "Import", "Importer"),
	export("تصدير", "Export", "Exporter"),
	help("مساعدة", "Help", "Aide"),
	about_us("حول التّطبيق", "About", "A propos"),
	events("الأحداث", "Events", "Evènements"),
	type("النّوع", "Type", "Type"),
	focus("وضع التّركيز", "Focus mode", "Mode concentration"),
	store_failed("فشل التّخزين", "Failed storing", "Stockage échoué"),
	store_max("لا يمكن تجاوز العدد المسموح للتّخزين", "Cannot store more", "Ne peut pas dépasser la limite du stockage"),
	focus_exit("للخروج اضغط على Alt + F", "To exit click Alt + F", "Pour sortir appuyer Alt + F"),
	thinkBAY("مفكّرة بن حليمة أحمد يوسف", "ThinkBAY", "ThinkBAY"),
	connection("اتّصال","Connection","Connexion"),
	local_db("قاعدة البيانات المحلّيّة","Local DB","BDD locale"),
	extern_db("قاعدة البيانات الخارجيّة","Extern DB","BDD externe"),
	ftp("FTP","FTP","FTP"),
	host("المضيف","Host","Hébèrgement"),
	port("مدخل","Port","Porte"),
	database("قاعدة البيانات","Database","Base de données"),
	user("المستخدم","User","Utilisateur"),
	password("كلمة المرور","Password","Mot de passe"),
	mysql_version("نسخة MySQL","MySQL version","Version de MySQL"),
	language("اللّغة","Language","Langage"),
	choose_ideas_dir("اختر مجلّد التّخزين","Choose load folder","Choisir le  dossier du chargement"),
	max_results("العدد الأقصى لنتائج الصّفحات","Max number of page results","Le nombre maximale des résultats du page"),
	tray_notifs("الإشعارات المنبثقة","Popup notifications","Popup des notifications"),
	accounts("الحسابات","Accounts","Comptes"),
	device_id("تعريف الجهاز","Device ID","ID d'appareil"),
	allowed("مسموح","Allowed","Autorisé"),
	connected("متّصل","Connected","Connecté"),
	not_connected("غير متّصل","Not Connected","Non Connecté"),
	change("تغيير","Change","Changer"),
	enter("دخول","Connect","Connecter"),
	allow_new("السّماح بالدّخول للحسابات الجديدة","Allow new accounts","Autoriser l'accés pour les nouveaux comptes"),
	last_visit("آخر زيارة","Last visit","Dernière visite"),
	disconnect("إلغاء الاتّصال بالحساب","Disconnect","Déconnexion"),
	similary_to("الموافق لـ","corresponding to","correspond à"),
	database_status("الاتّصال بقاعدة البيانات","Connection to DB","Connexion au BDD"),
	available("متاح","Available","Disponible"),
	unavailable("غير متاح","Unavailable","Indisponible"),
	local("محليّ","Local","Locale"),
	extern("خارجيّ","Extern","Externe"),
	account("الحساب","Account","Compte"),
	ideas_num("عدد الأفكار","Ideas number","Nombre des idées"),
	attachs_num("عدد المرفقات","Attachments number","Nombre des attachements"),
	worknotes_num("عدد ملاحظات العمل","WorkNotes number","Nombre de WorkNotes"),
	tasksnum("عدد المهمّات","Tasks number","Nombre de tâches"),
	error("خطأ","Error","Erreur"),
	success("تمّ","Done","Fait"),
	idea_saved("تمّ حفظ الفكرة","Idea saved","Idée sauvegardée"),
	idea_deleted("تمّ إزالة الفكرة","Idea deleted","Idée effacée"),
	idea_modified("تمّ تعديل الفكرة","Idea modified","Idée modifiée"),
	idea_refreshed("تمّ تحديث الفكرة","Idea refreshed","Idée actualisée"),
	results("تمّ البحث","Search done","Recherche faite"),
	modified("تمّ التّعديل","Modified","Modifié"),
	close("خروج","Exit","Sortir"),
	yes("نعم","Yes","Oui"),
	no("لا","No","Non"),
	work_degree("درجة العمل","Work degree","Degré du travail"),
	default_theme("المظهر الافتراضيّ","Default theme","Thème par défaut"),
	custom_theme("المظهر المخصّص","Custom theme","Thème personnalisé"),
	test("تجربة","Test","Test");
	
	private String ar;
	private String en;
	private String fr;
	
	private ThinkStr(String ar, String en, String fr) {
		this.ar = ar;
		this.en = en;
		this.fr = fr;
	}
	
	public String toString() {
		switch (ThinkUtil.getLang()) {
			case AR: return ar;
			case EN: return en;
			case FR: return fr;
		}
		return ar;
	}
	
}