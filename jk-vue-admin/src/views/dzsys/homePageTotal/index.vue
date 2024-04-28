<template>
    <div class="app-container">
        <div class="block">
            <el-row  :gutter="20">
                <el-col :span="4">
                    <el-input v-model="listQuery.id" size="mini" placeholder="请输入id"></el-input>
                </el-col>
                <el-col :span="6">
                    <el-button type="success" size="mini" icon="el-icon-search" @click.native="search">{{ $t('button.search') }}</el-button>
                    <el-button type="primary" size="mini" icon="el-icon-refresh" @click.native="reset">{{ $t('button.reset') }}</el-button>
                </el-col>
            </el-row>
            <br>
            <el-row>
                <el-col :span="24">
                    <el-button type="success" size="mini"  icon="el-icon-plus" @click.native="add" v-permission="['/dzsys/homepagetotal/add']">{{ $t('button.add') }}</el-button>
                    <el-button type="primary" size="mini"  icon="el-icon-edit" @click.native="edit" v-permission="['/dzsys/homepagetotal/update']">{{ $t('button.edit') }}</el-button>
                    <el-button type="danger" size="mini"  icon="el-icon-delete" @click.native="remove" v-permission="['/dzsys/homepagetotal/delete']">{{ $t('button.delete') }}</el-button>
                </el-col>
            </el-row>
        </div>


        <el-table  size="mini" :data="list" v-loading="listLoading" element-loading-text="Loading" border fit highlight-current-row
                  @current-change="handleCurrentChange">
            <el-table-column label="日期">
                <template slot-scope="scope">
                    {{scope.row.day}}
                </template>
            </el-table-column>
            <el-table-column label="注册人数">
                <template slot-scope="scope">
                    {{scope.row.registrationNum}}
                </template>
            </el-table-column>
            <el-table-column label="VIP新增人数">
                <template slot-scope="scope">
                    {{scope.row.vipNum}}
                </template>
            </el-table-column>
            <el-table-column label="VIP首充人数">
                <template slot-scope="scope">
                    {{scope.row.vipFirstNum}}
                </template>
            </el-table-column>
            <el-table-column label="充值数量">
                <template slot-scope="scope">
                    {{scope.row.cNum}}
                </template>
            </el-table-column>
            <el-table-column label="充值金额">
                <template slot-scope="scope">
                    {{scope.row.cMoney}}
                </template>
            </el-table-column>
            <el-table-column label="提现数量">
                <template slot-scope="scope">
                    {{scope.row.tNum}}
                </template>
            </el-table-column>
            <el-table-column label="提现金额">
                <template slot-scope="scope">
                    {{scope.row.tMoney}}
                </template>
            </el-table-column>
            <el-table-column label="平台盈利">
                <template slot-scope="scope">
                    {{scope.row.money}}
                </template>
            </el-table-column>
            <el-table-column label="L1VIP返佣">
                <template slot-scope="scope">
                    {{scope.row.l1Vip}}
                </template>
            </el-table-column>
            <el-table-column label="L2VIP返佣">
                <template slot-scope="scope">
                    {{scope.row.l2Vip}}
                </template>
            </el-table-column>
            <el-table-column label="L3VIP返佣">
                <template slot-scope="scope">
                    {{scope.row.l3Vip}}
                </template>
            </el-table-column>
            <el-table-column label="L1任务返佣">
                <template slot-scope="scope">
                    {{scope.row.l1Pb}}
                </template>
            </el-table-column>
            <el-table-column label="L2任务返佣">
                <template slot-scope="scope">
                    {{scope.row.l2Pb}}
                </template>
            </el-table-column>
            <el-table-column label="L3任务返佣">
                <template slot-scope="scope">
                    {{scope.row.l3Pb}}
                </template>
            </el-table-column>
            <el-table-column label="发放总佣金">
                <template slot-scope="scope">
                    {{scope.row.totalPb}}
                </template>
            </el-table-column>
            <el-table-column label="注册奖励">
                <template slot-scope="scope">
                    {{scope.row.dcMoney}}
                </template>
            </el-table-column>
            <el-table-column label="完成任务数量">
                <template slot-scope="scope">
                    {{scope.row.pbNum}}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button type="text" size="mini" icon="el-icon-edit" @click.native="editItem(scope.row)" v-permission="['/dzsys/homepagetotal/update']">{{ $t('button.edit') }}</el-button>
                    <el-button type="text" size="mini" icon="el-icon-delete" @click.native="removeItem(scope.row)" v-permission="['/dzsys/homepagetotal/delete']">{{ $t('button.delete') }}</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination
                background
                layout="total, sizes, prev, pager, next, jumper"
                :page-sizes="[10, 20, 50, 100,500]"
                :page-size="listQuery.limit"
                :total="total"
                @size-change="changeSize"
                @current-change="fetchPage"
                @prev-click="fetchPrev"
                @next-click="fetchNext">
        </el-pagination>

        <el-dialog
                :title="formTitle"
                :visible.sync="formVisible"
                width="70%">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="日期"  >
                            <el-input v-model="form.day" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="注册人数"  >
                            <el-input v-model="form.registrationNum" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="VIP新增人数"  >
                            <el-input v-model="form.vipNum" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="VIP首充人数"  >
                            <el-input v-model="form.vipFirstNum" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="充值数量"  >
                            <el-input v-model="form.cNum" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="充值金额"  >
                            <el-input v-model="form.cMoney" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="提现数量"  >
                            <el-input v-model="form.tNum" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="提现金额"  >
                            <el-input v-model="form.tMoney" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="平台盈利"  >
                            <el-input v-model="form.money" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="L1VIP返佣"  >
                            <el-input v-model="form.l1Vip" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="L2VIP返佣"  >
                            <el-input v-model="form.l2Vip" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="L3VIP返佣"  >
                            <el-input v-model="form.l3Vip" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="L1任务返佣"  >
                            <el-input v-model="form.l1Pb" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="L2任务返佣"  >
                            <el-input v-model="form.l2Pb" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="L3任务返佣"  >
                            <el-input v-model="form.l3Pb" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="发放总佣金"  >
                            <el-input v-model="form.totalPb" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="注册奖励"  >
                            <el-input v-model="form.dcMoney" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="完成任务数量"  >
                            <el-input v-model="form.pbNum" minlength=1></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item>
                    <el-button type="primary" @click="save">{{ $t('button.submit') }}</el-button>
                    <el-button @click.native="formVisible = false">{{ $t('button.cancel') }}</el-button>
                </el-form-item>

            </el-form>
        </el-dialog>
    </div>
</template>

<script src="./homePageTotal.js"></script>


<style rel="stylesheet/scss" lang="scss" scoped>
    @import "src/styles/common.scss";
</style>

