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
        <b-pagination
                v-model="currentPage"
                :total-rows="students.length"
                :per-page="perPage"
                aria-controls="students-table"
                class="m-3 dark"
        ></b-pagination>
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
                students: []
            }
        },
        methods: {
            sex(a) {
                if (a)
                    return 'M';
                return 'W'
            },
            save(students) {
                this.students = students;
            }
        },
        mounted() {
            this.$client
                .get('/students')
                .then(response => {
                    this.students = response.data;
                });
        }
    }
</script>

<style scoped>
    h2 {
        text-align: left;
    }
</style>