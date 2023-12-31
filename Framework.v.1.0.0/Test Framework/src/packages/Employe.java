package packages;

import framework.annotation.Session;
import framework.annotation.Scope;
import framework.annotation.Rest;
import framework.ModelView;
import framework.utility.FileUpload;
import framework.annotation.Auth;
import framework.annotation.Url;
import framework.annotation.RequestParameter;

import java.util.HashMap;
import java.util.Vector;
import java.sql.Date;

@Scope( name = "Singleton" )
public class Employe{

	String name;
	Integer id;
	Date date;
	String[] days;
	FileUpload badge;

	// Add HashMap variable for storing datas
	HashMap<String, Object> sessions = new HashMap<String, Object>();

	public void setDays(String[] days){
		this.days = days;
	}

	public String[] getDays(){
		return this.days;
	}

	public Employe(){}
	public Employe(String name){
		this.setName(name);
		this.setId(500);
	}
	public Employe(String name , int id ){
		this.setName(name);
		this.setId(id);
	}

	public void setBadge(FileUpload upload){
		this.badge = upload;
	}

	public void setSessions( HashMap<String , Object> sessions ){
		this.sessions = sessions;
	}

	public HashMap<String, Object> getSessions(){
		return this.sessions;
	}

	public FileUpload getBadge(){
		return this.badge;
	}

	public void setId( Integer id ){
		this.id = id;
	}

	public Integer getId(){
		return this.id;
	}

	public void setName(String n){
		this.name = n;
	}
	public String getName(){
		return this.name;
	}

	public void setDate( Date e ){
		this.date = e;
	}
	public Date getDate(){
		return this.date;
	}

	@Url( url="/emp-index" )
	public ModelView index()  throws Exception{
		Vector<Employe> emps = new Vector<Employe>();
		emps.add( new Employe("Sarobidy") );
		emps.add( new Employe("Rodolphe") );
		emps.add( new Employe("Fanilo") );
//		bonjour je mange des enfants
		ModelView returns = new ModelView("employe.jsp");
		returns.addItem("emp-list" , emps); 
		return returns;
	}

	@Session()
	@Auth()
	@Url( url = "/emp-add" )
	public ModelView addEmp() throws Exception{
		Vector<Employe> emps = new Vector<Employe>();
		emps.add( this );
		ModelView returns = new ModelView("employe.jsp");
		returns.addItem("emp-list" , emps); 
		return returns;
	}

	@Url( url = "/find" )
	public ModelView findById( @RequestParameter(name = "id" ) Integer id )  throws Exception{
	// public ModelView findById( Integer id ){
		Vector<Employe> emps = new Vector<Employe>();
		emps.add( new Employe("Sarobidy" , 1) );
		emps.add( new Employe("Sarobidy Manoary" , 2) );
		emps.add( new Employe("Sarobidy 2" , 3) );
		emps.add( new Employe("Sarobidy 3" , 4) );
		emps.add( new Employe("Sarobidy 4" , 5) );

		ModelView mv = new ModelView("Result.jsp");
		mv.setJson( true );
		Employe retour = null;
		for( Employe e : emps ){
			if( e.getId() == id ){
				retour = e;
				break;
			}
		}
		mv.addItem("Employe" , retour);
		return mv;
	}

	@Rest
	@Url( url = "/getSarobidy" )
	public Employe getSarobidy() {
		Employe employe = new Employe( "Sarobidy" , 0341422651 );
		return employe;
	}

	@Url( url = "/log-out" )
	public ModelView logout()  throws Exception{
		ModelView modelview = new ModelView("index.jsp");
		modelview.invalidateSession(true);
		return modelview;
	}

}