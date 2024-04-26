import CodeHighlighting from '@/components/CodeHighlighting';
import {errorCode} from '@/model/enums/ErrorCodeEnum';
import {interfaceMethodList, interfaceStatusList} from "@/model/enums/interfaceInfoEnum";
import {
  axiosExample,
  javaExample,
  requestParameters,
  responseParameters,
  returnExample
} from '@/pages/InterfaceInfo/components/defaultCode';
import ToolsTab from "@/pages/InterfaceInfo/components/ToolsTab";
import {
  invokeInterfaceInfoUsingPost,
  getInterfaceInfoByIdUsingGet
} from "@/services/yunfeiapi-backend/interfaceInfoController";

import {useParams} from '@@/exports';
import {
  BugOutlined,
  CodeOutlined,
  FileExclamationOutlined,
  FileTextOutlined,
} from '@ant-design/icons';
import {PageContainer, ProColumns} from '@ant-design/pro-components';
import {Badge, Card, Descriptions, Divider, Form, message, Select, Table, Tabs} from 'antd';
import {Column} from 'rc-table';
import React, {useEffect, useState} from 'react';
import './index.less';

const {Option} = Select;

type toolsParams = {
  id: number;
  paramsName: string;
  paramsValue: string;
};

const InterfaceInfo: React.FC = () => {
  // 定义状态和钩子函数
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfoVO>();
  const params = useParams();
  const [activeTabKey, setActiveTabKey] = useState<
    'tools' | 'api' | 'errorCode' | 'sampleCode' | string
  >('api');
  const [form] = Form.useForm();
  const [requestExampleActiveTabKey, setRequestExampleActiveTabKey] = useState<string>('javadoc');
  const [javaCode, setJavaCode] = useState<any>();
  const [axiosCode, setAxiosCode] = useState<any>();

  // 在线调试工具输入框提示
  const [toolsInputPlaceholderValue, setToolsInputPlaceholderValue] = useState<string>();
  // 在线调试工具输入框的值
  const [toolsInputValue, setToolsInputValue] = useState<string>();
  // 请求参数列表数据
  const [toolsProEditTableData, setToolsProEditTableData] = useState<toolsParams[]>([]);
  const [toolsProEditTableDefaultData, setToolsProEditTableDefaultData] = useState<
    readonly toolsParams[]
  >([{id: 1, paramsName: '名称1', paramsValue: '参数1'}]);
  const [toolsResultLoading, setToolsResultLoading] = useState(false);
  const [toolsResult, setToolsResult] = useState<string>();

  // 请求、响应参数
  const [requestParams, setRequestParams] = useState<string>();
  const [responseParams, setResponseParams] = useState<string>();

  /**
   * 返回状态码
   */
  const [returnCode, setReturnCode] = useState<any>(returnExample);

  // 解析出的地址
  const [exampleUrl, setExampleUrl] = useState<string>('');
  const [exampleParams, setExampleParams] = useState<
    {
      key: string;
      value: string;
    }[]
  >([]);

  /**
   * 转换响应参数
   * @param params
   */
  const convertResponseParams = (params?: [API.RequestParamsField]) => {
    if (!params || params.length <= 0) {
      return returnExample;
    }
    const result = {};
    const codeObj = {};
    const messageObj = {};
    console.log("params", params)
    params.forEach((param) => {
      // @ts-ignore
      const keys = param.fieldName.split('.');
      // @ts-ignore
      let currentObj;
      if (keys[0] === 'code') {
        currentObj = codeObj;
      } else if (keys[0] === 'message') {
        currentObj = messageObj;
      } else {
        currentObj = result;
      }

      keys.forEach((key, index) => {
        if (index === keys.length - 1) {
          if (param.type === 'int' && key === 'code') {
            // @ts-ignore
            currentObj[key] = 0;
          } else {
            // @ts-ignore
            currentObj[key] = param.desc || '';
          }
        } else {
          // @ts-ignore
          currentObj[key] = currentObj[key] || {};
          // @ts-ignore
          currentObj = currentObj[key];
        }
      });
    });
    // @ts-ignore
    const mergedResult = {code: codeObj.code, ...result, message: messageObj.message};
    return JSON.stringify(mergedResult, null, 2);
  };

  const selectBefore = (
    <Select defaultValue={data?.method}>
      <Option value={interfaceMethodList.GET.text}>GET</Option>
      <Option value={interfaceMethodList.POST.text}>POST</Option>
      <Option value={interfaceMethodList.PUT.text}>PUT</Option>
      <Option value={interfaceMethodList.DELETE.text}>DELETE</Option>
    </Select>
  );

  // 在 toolsProEditTableData 中新增一行
  const handleProEditTableAdd = () => {
    const newData: toolsParams = {
      id: Date.now(),
      paramsName: '',
      paramsValue: '',
    };
    setToolsProEditTableData((prevData) => [...prevData, newData]);
  };

  const toolsInputDoubleClick = () => {
    if (toolsInputValue === '' || toolsInputValue === undefined) {
      setToolsInputValue(toolsInputPlaceholderValue);
      const params = exampleParams;
      const toolsProEditTableArr: toolsParams[] = [];
      for (let i = 0; i < params.length; i++) {
        toolsProEditTableArr.push({
          id: i + 1,
          paramsName: params[i].key,
          paramsValue: params[i].value,
        });
        setToolsProEditTableData((prevData) => [...prevData, toolsProEditTableArr[i]]);
      }
    }
  };
  const toolsInputChange = (even) => {
    setToolsInputValue(even.target.value);
  };

  const requestExampleTabChange = (key: string) => {
    setRequestExampleActiveTabKey(key);
  };

  /**
   * 在线调试工具默认显示的输入框
   */
  const toolsInputPlaceholder = () => {
    let resPlaceholder = '';
    // 健壮性校验
    const method = data?.method;
    if (method === undefined) {
      setToolsInputPlaceholderValue(resPlaceholder);
      return;
    }
    const url: string | undefined = data?.requestExample;
    if (url === undefined || url === null) {
      setToolsInputPlaceholderValue(resPlaceholder);
      return;
    }

    // GET
    if (method === interfaceMethodList.GET.text) {
      // 获取url?以前的字符
      resPlaceholder = url;
      // 判断是否有参数
      if (url.indexOf('?') !== -1) {
        const urlBefore = url.substring(0, url.indexOf('?'));
        // 获取参数
        const params1 = getParams(url);
        setExampleParams(params1);

        resPlaceholder = urlBefore;
      }
      setToolsInputPlaceholderValue(resPlaceholder);
      return;
    }
    // POST
    if (method === interfaceMethodList.POST.text) {
      // 获取请求参数requestParams里的参数列表
      // 判断是否有参数
      resPlaceholder = url;
      if (data?.requestParams === undefined || data?.requestParams === '') {
        setToolsInputPlaceholderValue(resPlaceholder);
        return;
      }
      // @ts-ignore
      const parsedData = JSON.parse(data?.requestParams);
      // 获取json中的fildName字段并放到一个集合中
      const params1 = parsedData.map((item) => ({key: item.fieldName, value: ''}));
      setExampleParams(params1);
      setToolsInputPlaceholderValue(resPlaceholder);
      return;
    }

    // 其他请求
    resPlaceholder = url;
    setToolsInputPlaceholderValue(resPlaceholder);
    return;
  };

  /**
   * 获取params
   */
  const getParams = (url: string) => {
    const urlObj = new URL(url);
    const params1 = new URLSearchParams(urlObj.search);
    const result: {
      key: string;
      value: string;
    }[] = [];

    for (const [key, value] of params1.entries()) {
      result.push({key, value});
    }

    return result;
  };

  /**
   * 提交在线调试
   */
  const submitTools = async () => {
    // 校验地址和参数是否输入
    if (toolsInputValue === '') {
      message.error('请填写地址');
    }
    setToolsResultLoading(true);
    const requestParams = toolsProEditTableData.reduce((acc, item) => {
      acc[item.paramsName] = item.paramsValue;
      return acc;
    }, {} as Record<string, any>);
    const res = await invokeInterfaceInfoUsingPost({id: data?.id, requestParams: requestParams});
    setToolsResult(JSON.stringify(res, null, 4));
    setToolsResultLoading(false);
  };

  const toolsParamsColumns: ProColumns<toolsParams>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'text',
      hideInTable: true,
      search: false,
    },
    {
      title: '参数名称',
      dataIndex: 'paramsName',
      valueType: 'text',
      search: false,
    },
    {
      title: '参数值',
      dataIndex: 'paramsValue',
      valueType: 'text',
      search: false,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 200,
      render: (text, record, _, action) => [
        <a
          key="editable"
          onClick={() => {
            action?.startEditable?.(record.id);
          }}
        >
          编辑
        </a>,
        <a
          key="delete"
          onClick={() => {
            setToolsProEditTableData(toolsProEditTableData.filter((item) => item.id !== record.id));
          }}
        >
          删除
        </a>,
      ],
    },
  ];

  /**
   *
   */
  const responseExampleContentList: Record<string, React.ReactNode> = {
    api: (
      <>
        <p className="highlightLine" style={{marginTop: 15}}>
          请求参数说明：<a onClick={() => setActiveTabKey('sampleCode')}>见示例代码</a>
        </p>
        <Table
          dataSource={requestParams && requestParams.length > 0 ? requestParams : requestParameters as any}
          pagination={false}
          style={{maxWidth: 800}}
          size={'small'}
        >
          <Column title="参数名称" dataIndex="fieldName" key="fieldName"/>
          <Column title="必选" dataIndex="required" key="required"/>
          <Column title="类型" dataIndex="type" key="type"/>
          <Column title="描述" dataIndex="desc" key="desc"/>
        </Table>
        <p className="highlightLine" style={{marginTop: 15}}>
          响应参数说明：<a onClick={() => setActiveTabKey('errorCode')}>错误码参照</a>
        </p>
        <Table
          dataSource={
            responseParams && responseParams?.length > 0 ? responseParams : responseParameters as any
          }
          pagination={false}
          style={{maxWidth: 800}}
          size={'small'}
        >
          <Column title="参数名称" dataIndex="fieldName" key="fieldName"/>
          <Column title="类型" dataIndex="type" key="type"/>
          <Column title="描述" dataIndex="desc" key="desc"/>
        </Table>
        <p className="highlightLine" style={{marginTop: 15}}>
          返回示例：
        </p>
        <CodeHighlighting codeString={returnCode} language={'json'}/>
      </>
    ),
    tools: (
      <>
        <ToolsTab
          toolsInputPlaceholderValue={toolsInputPlaceholderValue as any}
          toolsInputValue={toolsInputValue as any}
          toolsInputDoubleClick={toolsInputDoubleClick}
          toolsInputChange={toolsInputChange}
          submitTools={submitTools}
          toolsProEditTableDefaultData={toolsProEditTableDefaultData}
          toolsProEditTableData={toolsProEditTableData}
          setToolsProEditTableData={setToolsProEditTableData}
          handleProEditTableAdd={handleProEditTableAdd}
          toolsParamsColumns={toolsParamsColumns}
          data={data}
          toolsResultLoading={toolsResultLoading}
          toolsResult={toolsResult}
          requestExampleActiveTabKey={requestExampleActiveTabKey} requestParam={requestParams}
          temporaryParams={responseParams} toolsParams={toolsProEditTableData}/>
      </>
    ),
    errorCode: (
      <>
        <p className="highlightLine">错误码：</p>
        <Table dataSource={errorCode} pagination={false} style={{maxWidth: 800}} size={'small'}>
          <Column title="参数名称" dataIndex="name" key="name"/>
          <Column title="错误码" dataIndex="code" key="code"/>
          <Column title="描述" dataIndex="des" key="des"/>
        </Table>
      </>
    ),
    sampleCode: (
      <>
        <Tabs
          defaultActiveKey="javadoc"
          centered
          onChange={requestExampleTabChange}
          items={[
            {
              key: 'javadoc',
              label: 'java',
              children: <CodeHighlighting codeString={javaCode} language={'java'}/>,
            },
            {
              key: 'javascript',
              label: 'axios',
              children: (
                <CodeHighlighting codeString={axiosCode} language={requestExampleActiveTabKey}/>
              ),
            },
          ]}
        />
      </>
    ),
  };
  /**
   * 修改tab状态
   * @param key
   */
  const responseExampleTabChange = (key: string) => {
    if (key === 'tools') {
      toolsInputPlaceholder();
    }
    setActiveTabKey(key);
  };
  /**
   * tab列表
   */
  const responseExampleTabList = [
    {
      key: 'api',
      tab: (
        <>
          <FileTextOutlined/>
          API文档
        </>
      ),
    },
    {
      key: 'tools',
      tab: (
        <>
          <BugOutlined/>
          在线调试工具
        </>
      ),
    },
    {
      key: 'errorCode',
      tab: (
        <>
          <FileExclamationOutlined/>
          错误码参照
        </>
      ),
    },
    {
      key: 'sampleCode',
      tab: (
        <>
          <CodeOutlined/>
          示例代码
        </>
      ),
    },
  ];

  const loadData = async () => {
    // 初始化数据
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    setLoading(true);
    try {
      const res = await getInterfaceInfoByIdUsingGet({id: Number(params.id),});
      setData(res.data);

      if (res.data) { // 获取请求参数和响应参数
        const requestParams1 = res.data.requestParams;
        const responseParams1 = res.data.responseParams;
        try {
          setRequestParams(requestParams1 ? JSON.parse(requestParams1) : []);
          setResponseParams(responseParams1 ? JSON.parse(responseParams1) : []);
        } catch (e: any) {
          setRequestParams([] as any);
          setResponseParams([] as any);
        }
        const response = res.data.responseParams
          ? JSON.parse(res.data.responseParams)
          : ([] as API.RequestParamsField);
        const convertedParams = convertResponseParams(response);
        setAxiosCode(axiosExample(res.data?.url, res.data?.method?.toLowerCase()));
        setJavaCode(javaExample(res.data?.url, res.data?.method?.toUpperCase()));
        setReturnCode(convertedParams);
      }
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);


  return (
    <PageContainer
      header={{
        breadcrumb: {},
      }}
    >
      <Card>
        {data ? (
          <Descriptions
            title={
              <div>
                <div>
                  <p className="highlightLine" style={{fontSize: 20}}>
                    {data.name}
                  </p>
                </div>
                <div style={{fontSize: '12px', color: '#C0C0C0'}}>{data.description}</div>
              </div>
            }
            column={3}
          >
            <Descriptions.Item label="请求地址">{data.url}</Descriptions.Item>
            <Descriptions.Item label="返回格式">{data.returnFormat}</Descriptions.Item>
            <Descriptions.Item label="请求方式">{data.method}</Descriptions.Item>
            <Descriptions.Item label="请求示例">{data.requestExample}</Descriptions.Item>
            <Descriptions.Item label="花费金币">{data.consumeCoins}个</Descriptions.Item>
            <Descriptions.Item label="接口状态">
              {data && data.status === 0 ? (
                <Badge
                  status={interfaceStatusList['0'].status as any}
                  text={interfaceStatusList['0'].text}
                />
              ) : null}
              {data && data.status === 1 ? (
                <Badge
                  status={interfaceStatusList['1'].status as any}
                  text={interfaceStatusList['1'].text}
                />
              ) : null}
            </Descriptions.Item>
          </Descriptions>
        ) : (
          <>接口不存在</>
        )}
      </Card>
      <Card>
        <p className="highlightLine">接口详细描述请前往开发者在线文档查看：</p>
        <a href={`http://doc.panyuwen.top/pages/${data?.id}/#${data?.name}`} target={"_blank"} rel="noreferrer">
          {/*<a href={`132/pages/${data?.id}/#${data?.name}`} target={'_blank'} rel="noreferrer">*/}
          📘 接口在线文档：{data?.name}
        </a>
      </Card>
      <Divider/>
      <Card
        tabList={responseExampleTabList as any}
        activeTabKey={activeTabKey}
        onTabChange={responseExampleTabChange}
      >
        {responseExampleContentList[activeTabKey]}
      </Card>
    </PageContainer>
  );
};

export default InterfaceInfo;
