import VueRouter from 'vue-router';
import IndexPage from "../components/IndexPage";
import StudentSearch from "../components/tables/StudentSearch";
import GroupSearch from "../components/tables/GroupSearch";
import FacultySearch from "../components/tables/FacultySearch";
import DepartmentSearch from "../components/tables/DepartmentsSearch";
import CoursesSearch from "../components/tables/CoursesSearch";
import SemesterSearch from "../components/tables/SemesterSearch";
import StatisticsPage from "../components/StatisticsPage";

const routes = [
    {
        path: '',
        component: IndexPage
    },
    {
        path: '/students',
        component: StudentSearch
    },
    {
        path: '/groups',
        component: GroupSearch
    },
    {
        path: '/faculties',
        component: FacultySearch
    },
    {
        path: '/departments',
        component: DepartmentSearch
    },
    {
        path: '/courses',
        component: CoursesSearch
    },
    {
        path: '/semesters',
        component: SemesterSearch
    },
    {
        path:'/stats',
        component: StatisticsPage
    }
];

const router = new VueRouter({
    mode: 'history',
    routes
});

export default router;