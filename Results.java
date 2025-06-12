public class Results {
  public String schedulingType;
  public String schedulingName;
  public int compuTime;
	public int quantum;

  public Results (String schedulingType, String schedulingName, int compuTime, int quantum) {
    this.schedulingType = schedulingType;
    this.schedulingName = schedulingName;
    this.compuTime = compuTime;
    this.quantum = quantum;
  } 	

	void setQuantum (int quantum){
		this.quantum = quantum;
	}
}
