<template>
    <div>
        <h2 class="m-4">Groups table</h2>
        <b-table id="groups-table" bordered hover
                 fixed
                 head-variant="light"
                 :items="groups"
                 :fields="fields"
                 :per-page="perPage"
                 :current-page="currentPage"
                 class="mb-2">
        </b-table>
        <div>
            <b-row class="w-100">
                <b-col>
                    <b-pagination
                            v-model="currentPage"
                            :total-rows="groups.length"
                            :per-page="perPage"
                            aria-controls="groups-table"
                            class="m-3 dark"
                    ></b-pagination>
                </b-col>
                <b-col>
                    <b-button v-b-modal.modal-adding-group class="m-3 float-right">Add</b-button>
                </b-col>
            </b-row>
        </div>
        <div>
            <b-modal
                    id="modal-adding-group"
                    ref="modal"
                    centered
                    title="Add Group"
                    @show="resetModal"
                    @hidden="resetModal"
                    @ok="handleOk"
            >
                <form ref="form">
                    <b-form-group
                            :state="numberState"
                            label="Number"
                            label-for="number-input"
                            invalid-feedback="Number is required"
                    >
                        <b-form-input
                                ref="number"
                                id="number-input"
                                v-model="number"
                                :state="numberState"
                                required
                        ></b-form-input>
                    </b-form-group>
                    <b-form-group
                            :state="facultyState"
                            label="Faculty"
                            label-for="faculty-input"
                            invalid-feedback="Faculty is required"
                    >
                        <b-form-input
                                ref="faculty"
                                id="faculty-input"
                                v-model="faculty"
                                :state="facultyState"
                                required
                        ></b-form-input>
                    </b-form-group>
                    <b-form-group
                            :state="departmentState"
                            label="Department"
                            label-for="faculty-input"
                            invalid-feedback="Department is required"
                    >
                        <b-form-input
                                ref="department"
                                id="department-input"
                                v-model="department"
                                :state="departmentState"
                                required
                        ></b-form-input>
                    </b-form-group>
                </form>
                <div class="err">
                    {{errorMessage}}
                </div>
            </b-modal>
        </div>
    </div>
</template>

<script>
    export default {
        name: "GroupSearch",
        data (){
            return {
                perPage:10,
                currentPage: 1,
                fields: [
                    {
                        key: 'number',
                        label: 'Number'
                    },
                    {
                        key: 'nameFaculty',
                        label: 'Faculty',
                        sortable: true
                    },
                    {
                        key: 'nameDepartment',
                        label: 'Department'
                    }
                ],
                groups: [],
                number: '',
                faculty: '',
                department: '',
                numberState: null,
                facultyState: null,
                departmentState: null,
                errorMessage: null
            }
        },
        mounted() {
            this.update();
        },
        methods: {
            checkFormValidity() {
                let validNumber = this.$refs.number.checkValidity();
                if (!Number(this.number))
                    validNumber = false;
                this.numberState = validNumber;

                let validFaculty = this.$refs.faculty.checkValidity();
                this.facultyState = !!validFaculty;

                let validDepartment = this.$refs.department.checkValidity();
                this.departmentState = !!validDepartment;

                return validNumber && validFaculty && validDepartment;
            },
            resetModal() {
                this.number = '';
                this.faculty = '';
                this.department = '';
                this.facultyState = null;
                this.numberState = null;
                this.departmentState = null;
                this.errorMessage = null;
            },
            handleOk(bvModalEvt) {
                // Prevent modal from closing
                bvModalEvt.preventDefault();
                this.errorMessage = null;

                if (!this.checkFormValidity()) {
                    return;
                }
                this.$client({
                    method: 'post',
                    url: '/add_group',
                    params: {
                        number: this.number,
                        faculty: this.faculty,
                        department: this.department
                    }
                })
                .then(() => (this.update()))
                .catch((er) => this.errorMessage = er.response.data)

                if(!this.errorMessage){
                    // eslint-disable-next-line no-console
                    console.log(this.errorMessage);
                    return;
                }

                this.$nextTick(() => {
                    this.$refs.modal.hide();
                })
            },
            update() {
                this.$client
                    .get('/groups')
                    .then(response => {
                        this.groups = response.data;
                    });
            }
        }
    }
</script>

<style scoped>
    h2 {
        text-align: left;
    }
    .err {
        color: darkred;
    }
</style>