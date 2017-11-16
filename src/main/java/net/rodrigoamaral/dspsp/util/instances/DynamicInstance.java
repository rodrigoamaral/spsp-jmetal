package net.rodrigoamaral.dspsp.util.instances;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DynamicInstance {

    private List<List<Integer>> edge_set1;
    private int task_number_minvalue;
    private int task_number_maxvalue;
    private int task_effort_mu_max;
    private int task_effort_mu_min;
    private int task_effort_sigma_max;
    private int task_effort_sigma_min;
    private int task_skill_min;
    private int task_skill_max;
    private int graph_e_v_rate_mu;
    private int graph_e_v_rate_sigma;
    private int employee_number_minvalue;
    private int employee_number_maxvalue;
    private float employee_salary_mu;
    private float employee_salary_sigma;
    private int employee_skill_min;
    private int employee_skill_max;
    private int skill_number_min;
    private int skill_number_max;
    private int skill_number;
    private List<Integer> skill_set;
    private int task_number;
    private List<Double> task_effort_mu;
    private List<Double> task_effort_sigma;
    private List<Double> task_effort_real_secnario;
    private List<List<Integer>> task_skill_set;
    private List<Integer> task_skill_number;
    private double temp;
    private int i;
    private int j;
    private int s;
    private List<Integer> task_headcount;
    private List<List<Integer>> edge_set;
    private double evrate;
    private int edge_number;
    private List<Integer> ab;
    private double temp1;
    private int index;
    private int employee_number;
    private List<Float> employee_salary;
    private List<Float> employee_salary_over;
    private List<List<Integer>> employee_skill_set;
    private List<List<Double>> employee_skill_proficieny_set;
    private List<Integer> employee_skill_number;
    private List<Double> temp_proficieny;
    private double proficiency;
    private List<Double> employee_maxded;
    private int n_bits;
    private int kkk;
    private List<Integer> Randperm;
    private int l1;
    private List<Integer> Randperm1;
    private int l2;
    private List<Double> MTBL;
    private List<Double> MTTR;
    private int labour_leave_nmb;
    private List<List<Double>> TBL;
    private List<List<Double>> TTR;
    private List<List<Double>> labour_leave_time;
    private List<List<Double>> labour_return_time;
    private int temp_index;
    private int MTBTA;
    private int newtask_nmb;
    private List<Double> newtask_arrivalrate;
    private List<Double> arrival_time;
    private List<Integer> job_weights;
    private List<Double> task_effort_mu_new;
    private List<Double> task_effort_sigma_new;
    private List<Integer> task_headcount_new;
    private List<Double> task_effort_real_secnario_new;
    private List<Double> task_effort_mu_total;
    private List<Double> task_effort_sigma_total;
    private List<Integer> task_headcount_total;
    private List<Double> task_effort_real_secnario_total;
    private List<List<Integer>> task_skill_set_new;
    private List<Integer> task_skill_number_new;
    private List<List<Integer>> task_skill_set_total;
    private List<Integer> task_skill_number_total;
    private List<Integer> rush_job_index;
    private List<Double> rushjob_arrival_time;
    private List<Double> time1;
    private List<Integer> index1;
    private List<Double> time2;
    private List<Integer> index2;
    private List<Double> dynamic_time;
    private List<Integer> dynamic_class;
    private List<Integer> dynamic_rushjob_number;
    private List<Integer> dynamic_labour_leave_number;
    private List<Integer> dynamic_labour_return_number;
    private List<List<Double>> Task_Proficieny_total;
    private List<Integer> available_task;
    private List<Integer> available_employee;
    private int l;
    private int rushjob_counter;
    private int task_total_number;
    private int mark;
    private int k;

    public List<List<Integer>> getEdge_set1() {
        return edge_set1;
    }

    public void setEdge_set1(List<List<Integer>> edge_set1) {
        this.edge_set1 = edge_set1;
    }

    public int getTask_number_minvalue() {
        return task_number_minvalue;
    }

    public void setTask_number_minvalue(int task_number_minvalue) {
        this.task_number_minvalue = task_number_minvalue;
    }

    public int getTask_number_maxvalue() {
        return task_number_maxvalue;
    }

    public void setTask_number_maxvalue(int task_number_maxvalue) {
        this.task_number_maxvalue = task_number_maxvalue;
    }

    public int getTask_effort_mu_max() {
        return task_effort_mu_max;
    }

    public void setTask_effort_mu_max(int task_effort_mu_max) {
        this.task_effort_mu_max = task_effort_mu_max;
    }

    public int getTask_effort_mu_min() {
        return task_effort_mu_min;
    }

    public void setTask_effort_mu_min(int task_effort_mu_min) {
        this.task_effort_mu_min = task_effort_mu_min;
    }

    public int getTask_effort_sigma_max() {
        return task_effort_sigma_max;
    }

    public void setTask_effort_sigma_max(int task_effort_sigma_max) {
        this.task_effort_sigma_max = task_effort_sigma_max;
    }

    public int getTask_effort_sigma_min() {
        return task_effort_sigma_min;
    }

    public void setTask_effort_sigma_min(int task_effort_sigma_min) {
        this.task_effort_sigma_min = task_effort_sigma_min;
    }

    public int getTask_skill_min() {
        return task_skill_min;
    }

    public void setTask_skill_min(int task_skill_min) {
        this.task_skill_min = task_skill_min;
    }

    public int getTask_skill_max() {
        return task_skill_max;
    }

    public void setTask_skill_max(int task_skill_max) {
        this.task_skill_max = task_skill_max;
    }

    public int getGraph_e_v_rate_mu() {
        return graph_e_v_rate_mu;
    }

    public void setGraph_e_v_rate_mu(int graph_e_v_rate_mu) {
        this.graph_e_v_rate_mu = graph_e_v_rate_mu;
    }

    public int getGraph_e_v_rate_sigma() {
        return graph_e_v_rate_sigma;
    }

    public void setGraph_e_v_rate_sigma(int graph_e_v_rate_sigma) {
        this.graph_e_v_rate_sigma = graph_e_v_rate_sigma;
    }

    public int getEmployee_number_minvalue() {
        return employee_number_minvalue;
    }

    public void setEmployee_number_minvalue(int employee_number_minvalue) {
        this.employee_number_minvalue = employee_number_minvalue;
    }

    public int getEmployee_number_maxvalue() {
        return employee_number_maxvalue;
    }

    public void setEmployee_number_maxvalue(int employee_number_maxvalue) {
        this.employee_number_maxvalue = employee_number_maxvalue;
    }

    public float getEmployee_salary_mu() {
        return employee_salary_mu;
    }

    public void setEmployee_salary_mu(float employee_salary_mu) {
        this.employee_salary_mu = employee_salary_mu;
    }

    public float getEmployee_salary_sigma() {
        return employee_salary_sigma;
    }

    public void setEmployee_salary_sigma(float employee_salary_sigma) {
        this.employee_salary_sigma = employee_salary_sigma;
    }

    public int getEmployee_skill_min() {
        return employee_skill_min;
    }

    public void setEmployee_skill_min(int employee_skill_min) {
        this.employee_skill_min = employee_skill_min;
    }

    public int getEmployee_skill_max() {
        return employee_skill_max;
    }

    public void setEmployee_skill_max(int employee_skill_max) {
        this.employee_skill_max = employee_skill_max;
    }

    public int getSkill_number_min() {
        return skill_number_min;
    }

    public void setSkill_number_min(int skill_number_min) {
        this.skill_number_min = skill_number_min;
    }

    public int getSkill_number_max() {
        return skill_number_max;
    }

    public void setSkill_number_max(int skill_number_max) {
        this.skill_number_max = skill_number_max;
    }

    public int getSkill_number() {
        return skill_number;
    }

    public void setSkill_number(int skill_number) {
        this.skill_number = skill_number;
    }

    public List<Integer> getSkill_set() {
        return skill_set;
    }

    public void setSkill_set(List<Integer> skill_set) {
        this.skill_set = skill_set;
    }

    public int getTask_number() {
        return task_number;
    }

    public void setTask_number(int task_number) {
        this.task_number = task_number;
    }

    public List<Double> getTask_effort_mu() {
        return task_effort_mu;
    }

    public void setTask_effort_mu(List<Double> task_effort_mu) {
        this.task_effort_mu = task_effort_mu;
    }

    public List<Double> getTask_effort_sigma() {
        return task_effort_sigma;
    }

    public void setTask_effort_sigma(List<Double> task_effort_sigma) {
        this.task_effort_sigma = task_effort_sigma;
    }

    public List<Double> getTask_effort_real_secnario() {
        return task_effort_real_secnario;
    }

    public void setTask_effort_real_secnario(List<Double> task_effort_real_secnario) {
        this.task_effort_real_secnario = task_effort_real_secnario;
    }

    public List<List<Integer>> getTask_skill_set() {
        return task_skill_set;
    }

    public void setTask_skill_set(List<List<Integer>> task_skill_set) {
        this.task_skill_set = task_skill_set;
    }

    public List<Integer> getTask_skill_number() {
        return task_skill_number;
    }

    public void setTask_skill_number(List<Integer> task_skill_number) {
        this.task_skill_number = task_skill_number;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public List<Integer> getTask_headcount() {
        return task_headcount;
    }

    public void setTask_headcount(List<Integer> task_headcount) {
        this.task_headcount = task_headcount;
    }

    public List<List<Integer>> getEdge_set() {
        return edge_set;
    }

    public void setEdge_set(List<List<Integer>> edge_set) {
        this.edge_set = edge_set;
    }

    public double getEvrate() {
        return evrate;
    }

    public void setEvrate(double evrate) {
        this.evrate = evrate;
    }

    public int getEdge_number() {
        return edge_number;
    }

    public void setEdge_number(int edge_number) {
        this.edge_number = edge_number;
    }

    public List<Integer> getAb() {
        return ab;
    }

    public void setAb(List<Integer> ab) {
        this.ab = ab;
    }

    public double getTemp1() {
        return temp1;
    }

    public void setTemp1(double temp1) {
        this.temp1 = temp1;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(int employee_number) {
        this.employee_number = employee_number;
    }

    public List<Float> getEmployee_salary() {
        return employee_salary;
    }

    public void setEmployee_salary(List<Float> employee_salary) {
        this.employee_salary = employee_salary;
    }

    public List<Float> getEmployee_salary_over() {
        return employee_salary_over;
    }

    public void setEmployee_salary_over(List<Float> employee_salary_over) {
        this.employee_salary_over = employee_salary_over;
    }

    public List<List<Integer>> getEmployee_skill_set() {
        return employee_skill_set;
    }

    public void setEmployee_skill_set(List<List<Integer>> employee_skill_set) {
        this.employee_skill_set = employee_skill_set;
    }

    public List<List<Double>> getEmployee_skill_proficieny_set() {
        return employee_skill_proficieny_set;
    }

    public void setEmployee_skill_proficieny_set(List<List<Double>> employee_skill_proficieny_set) {
        this.employee_skill_proficieny_set = employee_skill_proficieny_set;
    }

    public List<Integer> getEmployee_skill_number() {
        return employee_skill_number;
    }

    public void setEmployee_skill_number(List<Integer> employee_skill_number) {
        this.employee_skill_number = employee_skill_number;
    }

    public List<Double> getTemp_proficieny() {
        return temp_proficieny;
    }

    public void setTemp_proficieny(List<Double> temp_proficieny) {
        this.temp_proficieny = temp_proficieny;
    }

    public double getProficiency() {
        return proficiency;
    }

    public void setProficiency(double proficiency) {
        this.proficiency = proficiency;
    }

    public List<Double> getEmployee_maxded() {
        return employee_maxded;
    }

    public void setEmployee_maxded(List<Double> employee_maxded) {
        this.employee_maxded = employee_maxded;
    }

    public int getN_bits() {
        return n_bits;
    }

    public void setN_bits(int n_bits) {
        this.n_bits = n_bits;
    }

    public int getKkk() {
        return kkk;
    }

    public void setKkk(int kkk) {
        this.kkk = kkk;
    }

    public List<Integer> getRandperm() {
        return Randperm;
    }

    @JsonProperty("Randperm")
    public void setRandperm(List<Integer> randperm) {
        Randperm = randperm;
    }

    public int getL1() {
        return l1;
    }

    public void setL1(int l1) {
        this.l1 = l1;
    }

    public List<Integer> getRandperm1() {
        return Randperm1;
    }

    @JsonProperty("Randperm1")
    public void setRandperm1(List<Integer> randperm1) {
        Randperm1 = randperm1;
    }

    public int getL2() {
        return l2;
    }

    public void setL2(int l2) {
        this.l2 = l2;
    }

    public List<Double> getMTBL() {
        return MTBL;
    }

    @JsonProperty("MTBL")
    public void setMTBL(List<Double> MTBL) {
        this.MTBL = MTBL;
    }

    public List<Double> getMTTR() {
        return MTTR;
    }

    @JsonProperty("MTTR")
    public void setMTTR(List<Double> MTTR) {
        this.MTTR = MTTR;
    }

    public int getLabour_leave_nmb() {
        return labour_leave_nmb;
    }

    public void setLabour_leave_nmb(int labour_leave_nmb) {
        this.labour_leave_nmb = labour_leave_nmb;
    }

    public List<List<Double>> getTBL() {
        return TBL;
    }

    @JsonProperty("TBL")
    public void setTBL(List<List<Double>> TBL) {
        this.TBL = TBL;
    }

    public List<List<Double>> getTTR() {
        return TTR;
    }

    @JsonProperty("TTR")
    public void setTTR(List<List<Double>> TTR) {
        this.TTR = TTR;
    }

    public List<List<Double>> getLabour_leave_time() {
        return labour_leave_time;
    }

    public void setLabour_leave_time(List<List<Double>> labour_leave_time) {
        this.labour_leave_time = labour_leave_time;
    }

    public List<List<Double>> getLabour_return_time() {
        return labour_return_time;
    }

    public void setLabour_return_time(List<List<Double>> labour_return_time) {
        this.labour_return_time = labour_return_time;
    }

    public int getTemp_index() {
        return temp_index;
    }

    public void setTemp_index(int temp_index) {
        this.temp_index = temp_index;
    }

    public int getMTBTA() {
        return MTBTA;
    }

    @JsonProperty("MTBTA")
    public void setMTBTA(int MTBTA) {
        this.MTBTA = MTBTA;
    }

    public int getNewtask_nmb() {
        return newtask_nmb;
    }

    public void setNewtask_nmb(int newtask_nmb) {
        this.newtask_nmb = newtask_nmb;
    }

    public List<Double> getNewtask_arrivalrate() {
        return newtask_arrivalrate;
    }

    public void setNewtask_arrivalrate(List<Double> newtask_arrivalrate) {
        this.newtask_arrivalrate = newtask_arrivalrate;
    }

    public List<Double> getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(List<Double> arrival_time) {
        this.arrival_time = arrival_time;
    }

    public List<Integer> getJob_weights() {
        return job_weights;
    }

    public void setJob_weights(List<Integer> job_weights) {
        this.job_weights = job_weights;
    }

    public List<Double> getTask_effort_mu_new() {
        return task_effort_mu_new;
    }

    public void setTask_effort_mu_new(List<Double> task_effort_mu_new) {
        this.task_effort_mu_new = task_effort_mu_new;
    }

    public List<Double> getTask_effort_sigma_new() {
        return task_effort_sigma_new;
    }

    public void setTask_effort_sigma_new(List<Double> task_effort_sigma_new) {
        this.task_effort_sigma_new = task_effort_sigma_new;
    }

    public List<Integer> getTask_headcount_new() {
        return task_headcount_new;
    }

    public void setTask_headcount_new(List<Integer> task_headcount_new) {
        this.task_headcount_new = task_headcount_new;
    }

    public List<Double> getTask_effort_real_secnario_new() {
        return task_effort_real_secnario_new;
    }

    public void setTask_effort_real_secnario_new(List<Double> task_effort_real_secnario_new) {
        this.task_effort_real_secnario_new = task_effort_real_secnario_new;
    }

    public List<Double> getTask_effort_mu_total() {
        return task_effort_mu_total;
    }

    public void setTask_effort_mu_total(List<Double> task_effort_mu_total) {
        this.task_effort_mu_total = task_effort_mu_total;
    }

    public List<Double> getTask_effort_sigma_total() {
        return task_effort_sigma_total;
    }

    public void setTask_effort_sigma_total(List<Double> task_effort_sigma_total) {
        this.task_effort_sigma_total = task_effort_sigma_total;
    }

    public List<Integer> getTask_headcount_total() {
        return task_headcount_total;
    }

    public void setTask_headcount_total(List<Integer> task_headcount_total) {
        this.task_headcount_total = task_headcount_total;
    }

    public List<Double> getTask_effort_real_secnario_total() {
        return task_effort_real_secnario_total;
    }

    public void setTask_effort_real_secnario_total(List<Double> task_effort_real_secnario_total) {
        this.task_effort_real_secnario_total = task_effort_real_secnario_total;
    }

    public List<List<Integer>> getTask_skill_set_new() {
        return task_skill_set_new;
    }

    public void setTask_skill_set_new(List<List<Integer>> task_skill_set_new) {
        this.task_skill_set_new = task_skill_set_new;
    }

    public List<Integer> getTask_skill_number_new() {
        return task_skill_number_new;
    }

    public void setTask_skill_number_new(List<Integer> task_skill_number_new) {
        this.task_skill_number_new = task_skill_number_new;
    }

    public List<List<Integer>> getTask_skill_set_total() {
        return task_skill_set_total;
    }

    public void setTask_skill_set_total(List<List<Integer>> task_skill_set_total) {
        this.task_skill_set_total = task_skill_set_total;
    }

    public List<Integer> getTask_skill_number_total() {
        return task_skill_number_total;
    }

    public void setTask_skill_number_total(List<Integer> task_skill_number_total) {
        this.task_skill_number_total = task_skill_number_total;
    }

    public List<Integer> getRush_job_index() {
        return rush_job_index;
    }

    public void setRush_job_index(List<Integer> rush_job_index) {
        this.rush_job_index = rush_job_index;
    }

    public List<Double> getRushjob_arrival_time() {
        return rushjob_arrival_time;
    }

    public void setRushjob_arrival_time(List<Double> rushjob_arrival_time) {
        this.rushjob_arrival_time = rushjob_arrival_time;
    }

    public List<Double> getTime1() {
        return time1;
    }

    public void setTime1(List<Double> time1) {
        this.time1 = time1;
    }

    public List<Integer> getIndex1() {
        return index1;
    }

    public void setIndex1(List<Integer> index1) {
        this.index1 = index1;
    }

    public List<Double> getTime2() {
        return time2;
    }

    public void setTime2(List<Double> time2) {
        this.time2 = time2;
    }

    public List<Integer> getIndex2() {
        return index2;
    }

    public void setIndex2(List<Integer> index2) {
        this.index2 = index2;
    }

    public List<Double> getDynamic_time() {
        return dynamic_time;
    }

    public void setDynamic_time(List<Double> dynamic_time) {
        this.dynamic_time = dynamic_time;
    }

    public List<Integer> getDynamic_class() {
        return dynamic_class;
    }

    public void setDynamic_class(List<Integer> dynamic_class) {
        this.dynamic_class = dynamic_class;
    }

    public List<Integer> getDynamic_rushjob_number() {
        return dynamic_rushjob_number;
    }

    public void setDynamic_rushjob_number(List<Integer> dynamic_rushjob_number) {
        this.dynamic_rushjob_number = dynamic_rushjob_number;
    }

    public List<Integer> getDynamic_labour_leave_number() {
        return dynamic_labour_leave_number;
    }

    public void setDynamic_labour_leave_number(List<Integer> dynamic_labour_leave_number) {
        this.dynamic_labour_leave_number = dynamic_labour_leave_number;
    }

    public List<Integer> getDynamic_labour_return_number() {
        return dynamic_labour_return_number;
    }

    public void setDynamic_labour_return_number(List<Integer> dynamic_labour_return_number) {
        this.dynamic_labour_return_number = dynamic_labour_return_number;
    }

    public List<List<Double>> getTask_Proficieny_total() {
        return Task_Proficieny_total;
    }

    @JsonProperty("Task_Proficieny_total")
    public void setTask_Proficieny_total(List<List<Double>> task_Proficieny_total) {
        Task_Proficieny_total = task_Proficieny_total;
    }

    public List<Integer> getAvailable_task() {
        return available_task;
    }

    public void setAvailable_task(List<Integer> available_task) {
        this.available_task = available_task;
    }

    public List<Integer> getAvailable_employee() {
        return available_employee;
    }

    public void setAvailable_employee(List<Integer> available_employee) {
        this.available_employee = available_employee;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getRushjob_counter() {
        return rushjob_counter;
    }

    public void setRushjob_counter(int rushjob_counter) {
        this.rushjob_counter = rushjob_counter;
    }

    public int getTask_total_number() {
        return task_total_number;
    }

    public void setTask_total_number(int task_total_number) {
        this.task_total_number = task_total_number;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }
}
