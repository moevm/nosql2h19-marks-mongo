<template>
    <div>
        <h2 class="m-4">Students table</h2>
        <b-table id="students-table" bordered hover
                 head-variant="light"
                 :items="students"
                 :fields="fields"
                 :per-page="perPage"
                 :current-page="currentPage"
                 class="mb-2">
            <template v-slot:cell(sex)="data">
                {{ data.item.sex ? 'M' : 'W'}}
            </template>
            <template v-slot:cell(group)="data">
                {{ data.item.groups[0]}}
            </template>
        </b-table>
        <div>
            <b-row class="w-100">
                <b-col>
                    <b-pagination
                            v-model="currentPage"
                            :total-rows="students.length"
                            :per-page="perPage"
                            aria-controls="students-table"
                            class="m-3 dark"
                    ></b-pagination>
                </b-col>
                <b-col>
                    <b-button v-b-modal.modal-adding-student class="m-3 float-right">Add</b-button>
                </b-col>
            </b-row>
        </div>
        <div>
            <b-modal
                    id="modal-adding-student"
                    ref="modal"
                    centered
                    title="Add Student"
                    @show="resetModal"
                    @hidden="resetModal"
                    @ok="handleOk"
            >
                <form ref="form">
                    <b-form-group
                            :state="nameState"
                            label="Name"
                            label-for="name-input"
                            invalid-feedback="Name is required"
                    >
                        <b-form-input
                                ref="name"
                                id="name-input"
                                v-model="name"
                                :state="nameState"
                                required
                        ></b-form-input>
                    </b-form-group>
                    <b-form-group
                            :state="surnameState"
                            label="Surname"
                            label-for="surname-input"
                            invalid-feedback="Surname is required"
                    >
                        <b-form-input
                                ref="surname"
                                id="surname-input"
                                v-model="surname"
                                :state="surnameState"
                                required
                        ></b-form-input>
                    </b-form-group>
                    <b-form-group
                            :state="sexfState"
                            label="Sex"
                            label-for="sexf-input"
                            invalid-feedback="Sex is required"
                    >
                        <b-form-input
                                ref="sexf"
                                id="sexf-input"
                                v-model="sexf"
                                :state="sexfState"
                                required
                        ></b-form-input>
                    </b-form-group>
                    <b-form-group
                            :state="groupState"
                            label="Group"
                            label-for="group-input"
                            invalid-feedback="Group is required"
                    >
                        <b-form-input
                                ref="group"
                                id="group-input"
                                v-model="group"
                                :state="groupState"
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
        name: "StudentSearch",
        data (){
            return {
                perPage:3,
                currentPage: 1,
                fields: ['name', 'surname', 'sex', 'group'],
                students: [],
                name: '',
                surname:'',
                sexf:'',
                group:'',
                nameState: null,
                surnameState:null,
                sexfState:null,
                groupState:null,
                errorMessage: null
            }
        },
        methods: {
            sex(a) {
                if (a)
                    return 'M';
                return 'W';
            },
            checkFormValidity() {
                let validName = this.$refs.name.checkValidity();
                this.nameState = !!validName;

                let validSurname = this.$refs.surname.checkValidity();
                this.surnameState = !!validSurname;

                let validSex = this.$refs.sexf.checkValidity();
                if (this.sexf != 'M' && this.sexf != 'W') {
                    validSex = false;
                }
                this.sexfState = validSex;

                let validGroup = this.$refs.group.checkValidity();
                if (!Number(this.group))
                    validGroup = false;
                this.groupState = validGroup;

                return validGroup && validName && validSex && validGroup;
            },
            resetModal() {
                this.name =  '';
                this.surname = '';
                this.sexf = '';
                this.group = '';
                this.nameState =  null;
                this.surnameState = null;
                this.sexfState = null;
                this.groupState = null;
                this.errorMessage =  null;
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
                    url: '/add_student',
                    params: {
                        name: this.name,
                        surname: this.surname,
                        sex: this.antisex(),
                        group: this.group
                    }
                })
                .then(() => (this.update()))
                .catch((er) => this.errorMessage = er.response.data)

                if(!this.errorMessage){
                    return;
                }

                this.$nextTick(() => {
                    this.$refs.modal.hide();
                })
            },
            update() {
                this.$client
                    .get('/students')
                    .then(response => {
                        this.students = response.data;
                    });
            },
            antisex() {
                if(this.sexf === 'M')
                    return 1;
                return 0;
            }
        },
        mounted() {
            this.update();
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