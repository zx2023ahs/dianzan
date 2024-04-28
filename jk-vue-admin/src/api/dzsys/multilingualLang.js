import request from '@/utils/request'
export default {
    getList:function(params) {
        return request({
            url: '/dzsys/mulutilinguallang/list',
            method: 'get',
            params
        })
    },
    add:function(params) {
        return request({
            url: '/dzsys/mulutilinguallang',
            method: 'post',
            data: params
        })
    },
    update:function(params) {
        return request({
            url: '/dzsys/mulutilinguallang',
            method: 'PUT',
            data: params
        })
    },
    remove:function(id) {
        return request({
            url: '/dzsys/mulutilinguallang',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
