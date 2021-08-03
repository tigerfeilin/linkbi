import { dataType } from './configs'

export function trimColType(str) {
  let colType
  let l_str = str.toLowerCase()
  if (str.indexOf('(') >= 0) {
    colType = dataType.find(type => type.name === l_str.split('(')[0])
  } else {
    colType = dataType.find(type => type.name === l_str)
  }
  if(!colType)
    colType = { name: 'default', needQuotation: true, availableFunc: ['count', 'none'] }
  return colType
}
export function buildSqlSentence({ dbType, dataSrc, selectedCalcul, selectedDimension, orderByStrs, filterStr, limit })
{
  if(dbType === undefined)
    return
  if(dbType === 'postgresql')
    return buildSqlSentence_pg({ dataSrc, selectedCalcul, selectedDimension, orderByStrs, filterStr, limit });
  else if(dbType === 'oracle')
    return buildSqlSentence_oracle({ dataSrc, selectedCalcul, selectedDimension, orderByStrs, filterStr, limit });
  else
    return buildSqlSentence_default({ dataSrc, selectedCalcul, selectedDimension, orderByStrs, filterStr, limit });
}
export function buildSqlSentence_default({ dataSrc, selectedCalcul, selectedDimension, orderByStrs, filterStr, limit }) {
  let fields
  const groups = []
  let groupBy
  let orderBy
  let where
  let allSelected = []
  allSelected = allSelected.concat(selectedCalcul).concat(selectedDimension)
  fields = allSelected.map(field => {
    const calculField = selectedCalcul.find(col => col.Column === field.Column)
    if (calculField && calculField.calculFunc !== 'none') {
      return `${calculField.calculFunc || trimColType(calculField.Type).availableFunc[0]}(${calculField.Column}) as ${calculField.Column}`
    } else {
      groups.push(field.Column)
      return field.Column
    }
  })
  if (fields.length === 0 || !dataSrc) return
  if (groups.length > 0) {
    groupBy = `GROUP BY ${groups.join()}`
  }
  if (orderByStrs.length > 0) {
    orderBy = `ORDER BY ${orderByStrs.join()}`
  }
  if (filterStr) {
    where = `where ${filterStr}`
  }
  return `SELECT ${fields.join()} FROM (${dataSrc}) as t ${where || ''} ${groupBy || ''} ${orderBy || ''} LIMIT ${limit || 200}`
}
export function buildSqlSentence_pg({ dataSrc, selectedCalcul, selectedDimension, orderByStrs, filterStr, limit }) {
  let fields
  const groups = []
  let groupBy
  let orderBy
  let where
  let allSelected = []
  allSelected = allSelected.concat(selectedCalcul).concat(selectedDimension)
  fields = allSelected.map(field => {
    const calculField = selectedCalcul.find(col => col.Column === field.Column)
    if (calculField && calculField.calculFunc !== 'none') {
      return `${calculField.calculFunc || trimColType(calculField.Type).availableFunc[0]}("${calculField.Column}") as "${calculField.Column}"`
    } else {
      groups.push('"'+field.Column + '"')
      return '"'+field.Column + '"'
    }
  })
  if (fields.length === 0 || !dataSrc) return
  if (groups.length > 0) {
    groupBy = `GROUP BY ${groups.join()}`
  }
  if (orderByStrs.length > 0) {
    orderBy = `ORDER BY ${orderByStrs.join()}`
  }
  if (filterStr) {
    where = `where ${filterStr}`
  }
  return `SELECT ${fields.join()} FROM (${dataSrc}) as t ${where || ''} ${groupBy || ''} ${orderBy || ''} LIMIT ${limit || 200}`
}
export function buildSqlSentence_oracle({ dataSrc, selectedCalcul, selectedDimension, orderByStrs, filterStr, limit }) {
  let fields
  const groups = []
  let groupBy
  let orderBy
  let where
  let allSelected = []
  allSelected = allSelected.concat(selectedCalcul).concat(selectedDimension)
  fields = allSelected.map(field => {
    const calculField = selectedCalcul.find(col => col.Column === field.Column)
    if (calculField && calculField.calculFunc !== 'none') {
      return `${calculField.calculFunc || trimColType(calculField.Type).availableFunc[0]}("${calculField.Column}") as ${calculField.Column}`
    } else {
      groups.push('"'+field.Column + '"')
      return '"'+field.Column + '"'
    }
  })
  if (fields.length === 0 || !dataSrc) return
  if (groups.length > 0) {
    groupBy = `GROUP BY ${groups.join()}`
  }
  if (orderByStrs.length > 0) {
    orderBy = `ORDER BY ${orderByStrs.join()}`
  }
  if (filterStr) {
    where = `where ${filterStr}`
  }
  return `SELECT * FROM (SELECT ${fields.join()} FROM (${dataSrc})  ${where || ''} ${groupBy || ''} ${orderBy || ''}) WHERE ROWNUM < ${limit || 200}`
}
export function buildSql_Filters({dbType,filters}) {
  if(dbType === 'postgresql')
    return filters.map(buildFilterSentence_pg)
  else
    return filters.map(buildFilterSentence_default)
}

export function buildFilterSentence_pg(filter) {
  let filterSentence
  let valueObj = filter.value
  if (trimColType(filter.colType).needQuotation) {
    valueObj = addQuotation(filter.value)
  }
  if (filter.operatorParamNum === 1) {
    filterSentence = `"${filter.filteCol}" ${filter.filterOperator} ${valueObj.value1}`
  } else if (filter.operatorParamNum === 2) {
    filterSentence = `"${filter.filteCol}" ${filter.filterOperator} ${valueObj.value1} and ${valueObj.value2}`
  } else {
    filterSentence = `"${filter.filteCol}" ${filter.filterOperator} ('${valueObj.arrValue.join(',')}')`
  }
  return filterSentence
  function addQuotation(valueObj) {
    return {
      value1: `'${valueObj.value1}'`,
      value2: `'${valueObj.value2}'`,
      arrValue: valueObj.arrValue.map(value => `'${value}'`)
    }
  }
}
export function buildFilterSentence_default(filter) {
  let filterSentence
  let valueObj = filter.value
  if (trimColType(filter.colType).needQuotation) {
    valueObj = addQuotation(filter.value)
  }
  if (filter.operatorParamNum === 1) {
    filterSentence = `${filter.filteCol} ${filter.filterOperator} ${valueObj.value1}`
  } else if (filter.operatorParamNum === 2) {
    filterSentence = `${filter.filteCol} ${filter.filterOperator} ${valueObj.value1} and ${valueObj.value2}`
  } else {
    filterSentence = `${filter.filteCol} ${filter.filterOperator} ('${valueObj.arrValue.join(',')}')`
  }
  return filterSentence
  function addQuotation(valueObj) {
    return {
      value1: `'${valueObj.value1}'`,
      value2: `'${valueObj.value2}'`,
      arrValue: valueObj.arrValue.map(value => `'${value}'`)
    }
  }
}
